package me.maiz.trainning.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/*")
public class ParamFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(ParamFilter.class);

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		Map<String, String[]> paramMap = req.getParameterMap();
		if (!paramMap.isEmpty()) {
			Set<Map.Entry<String, String[]>> params = paramMap.entrySet();
			StringBuilder paramBuilder = new StringBuilder();
			paramBuilder.append("{");
			for (Map.Entry<String, String[]> entry : params) {
				paramBuilder.append(entry.getKey());
				paramBuilder.append("=");
				if (entry.getValue().length == 1) {
					paramBuilder.append(entry.getValue()[0]);
				} else {
					paramBuilder.append(Arrays.toString(entry.getValue()));
				}
				paramBuilder.append(",");
			}
			paramBuilder.deleteCharAt(paramBuilder.length()-1);
			paramBuilder.append("}");
			String uri = req.getRequestURI();
			logger.info("请求路径:" + uri + " 参数为" + paramBuilder.toString());
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
