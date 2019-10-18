package com.woniuxy.mvc26;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.woniuxy.mvc26.annotations.Controller;
import com.woniuxy.mvc26.annotations.P;
import com.woniuxy.mvc26.annotations.RequestMapping;
import com.woniuxy.mvc26.annotations.ResponseBody;
import com.woniuxy.mvc26.exceptions.MVCException;
import com.woniuxy.mvc26.support.PackageScan;
import com.woniuxy.mvc26.support.PropertiesUtils;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -4480104559649863599L;

	/**
	 * uri <=> 方法 /hello <-> [TestController.sayHello]
	 */
	private Map<String, Method> URI_METHOD_MAP = new HashMap<>();
	Properties prop = null;

	@Override
	public void init() throws ServletException {

		// 得到controller所在的包
		prop = PropertiesUtils.load("mvc.properties");
		String basePackage = prop.getProperty("base.package");
		if (basePackage == null || basePackage.isEmpty() || basePackage.trim().isEmpty()) {
			throw new MVCException("请在mvc.properties中配置base.package项目");
		}

		// 扫描获得所有的Controller的class
		List<Class<?>> classes = PackageScan.scan(basePackage);
		for (Class<?> c : classes) {
			if (c.isAnnotationPresent(Controller.class)) {
				// 反射得到所有的class中的方法并解析注解
				System.out.println("[controller class]=>" + c.getName());
				Method[] handlerMethods = c.getMethods();
				for (Method m : handlerMethods) {
					RequestMapping rm = m.getAnnotation(RequestMapping.class);
					if (rm != null) {
						// 填充URI_METHOD_MAP
						URI_METHOD_MAP.put(rm.value(), m);
					}
				}
			} else {
				System.out.println("class[" + c.getName() + "]未加@Controller注解，忽略");
			}
		}
		System.out.println("Controller扫描结束，结果为" + URI_METHOD_MAP);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. 获取请求uri
		String fullUri = req.getRequestURI();
		String uri = fullUri.replace(req.getContextPath() + "/", "");
		System.out.println("uri:" + uri);

		// 2.找到对应的方法
		Method method = URI_METHOD_MAP.get(uri);
		if (method == null) {
			System.out.println("访问的资源不存在 uri:" + uri);
			String errorPage = prop.getProperty("404.page") != null ? prop.getProperty("404.page") : "error.jsp";

			req.setAttribute("msg", "对不起，这个页面在地球上没有~");
			req.getRequestDispatcher((!errorPage.startsWith("/")) ? "/" + errorPage : errorPage).forward(req, resp);

			return;
		}

		// 3. 参数绑定及类型转换
		Class<?> controllerClass = method.getDeclaringClass();
		// method -> 方法参数 -> @P -> 请求参数名字 -> 请求参数的值 -> 类型转换 -> 绑定
		Parameter[] params = method.getParameters();
		Object[] args = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			Parameter param = params[i];
			// 系统参数处理
			if (param.getType() == HttpServletRequest.class) {
				args[i] = req;
			} else if (param.getType() == HttpServletResponse.class) {
				args[i] = resp;
			} else if (param.getType() == HttpSession.class) {
				args[i] = req.getSession();
			} else {
				// 业务参数处理
				P p = param.getAnnotation(P.class);
				String reqParamName = p.value();

				// 避免复选框，多个数据情况，使用getParameterValues代替getParameter
				// username=["wanger"]
				String[] reqParamValues = req.getParameterValues(reqParamName);
				Class<?> paramType = param.getType();
				if (reqParamValues != null && reqParamValues.length > 0) {
					// 类型转换 string -> paramType
					if (paramType == String.class) {
						args[i] = reqParamValues[0];
					} else if (paramType == int.class || paramType == Integer.class) {
						args[i] = Integer.parseInt(reqParamValues[0]);
						// 其他类型转换
					} else if (paramType == List.class) {// List<String>
						args[i] = Arrays.asList(reqParamValues);
					} else if (paramType == Date.class) {// 2019-10-10 11:12:13
						if (reqParamValues.length > 0 && reqParamValues[0] != null) {
							String dateStr = reqParamValues[0];
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							try {
								args[i] = sdf.parse(dateStr);
							} catch (ParseException e) {
								throw new MVCException("日期格式解析出错： [" + dateStr + "]", e);
							}
						}
					} else {
						throw new MVCException("暂不支持的类型" + paramType);
					}
				}else {
					args[i]=null;
				}
			}
		}

		// 4.调用方法 method.invoke
		try {
			System.out.println("调用方法" + method.getName() + " 参数为" + Arrays.toString(args));

			Object result = method.invoke(controllerClass.newInstance(), args);

			// 5. 根据返回值跳转
			boolean isToJson = method.isAnnotationPresent(ResponseBody.class);
			if (isToJson) {
				// 转json
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
				String json = gson.toJson(result);
				resp.setContentType("application/json;charset=utf-8");
				resp.getWriter().print(json);
				return;
			}
			if (result instanceof String) {
				String resultStr = (String) result;
				// redirect操作
				if (resultStr.startsWith("redirect:")) {
					resp.sendRedirect(resultStr.substring(resultStr.indexOf(":") + 1));
				} else {
					req.getRequestDispatcher("/" + result.toString() + ".jsp").forward(req, resp);
				}
			} else {
				throw new MVCException("请返回String或者添加@ResponseBody");
			}

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
