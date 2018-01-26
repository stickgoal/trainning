package me.maiz.app.dailycost.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * 日志打印
 * Created by Lucas on 2017-03-08.
 */
@WebFilter("/*")
public class ParamLoggerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ParamLoggerFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder paramLogBuilder = new StringBuilder();
        paramLogBuilder.append("请求[" + requestURI + "]");
        if (parameterMap.size() > 0) {
            paramLogBuilder.append(",参数为");
            for (Map.Entry<String, String[]> e : parameterMap.entrySet()) {
                paramLogBuilder.append(e.getKey() + "=");
                if (e.getValue().length > 1) {
                    paramLogBuilder.append(Arrays.toString(e.getValue()) + ";");
                } else {
                    paramLogBuilder.append(e.getValue()[0] + ";");
                }
            }
        }
        logger.info(paramLogBuilder.toString());
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
