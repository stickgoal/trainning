package com.woniuxy.servelet02;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "CrossOriginFilter",urlPatterns = "/*")
public class CrossOrignFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 响应参数格式设置
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        HttpServletResponse response1 = (HttpServletResponse) response;
        /* 允许跨域的主机地址 */
        response1.setHeader("Access-Control-Allow-Origin", "*");
        /* 允许跨域的请求方法GET, POST, HEAD 等 */
        response1.setHeader("Access-Control-Allow-Methods", "*·");
        /* 重新预检验跨域的缓存时间 (s) */
        response1.setHeader("Access-Control-Max-Age", "3600");
        /* 允许跨域的请求头 */
        response1.setHeader("Access-Control-Allow-Headers", "*");
        /* 是否携带cookie */
        response1.setHeader("Access-Control-Allow-Credentials", "true");

        chain.doFilter(request, response1);
    }

    @Override
    public void destroy() {

    }
}

