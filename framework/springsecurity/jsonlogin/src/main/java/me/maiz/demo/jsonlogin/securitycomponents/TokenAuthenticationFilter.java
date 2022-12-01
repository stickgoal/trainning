package me.maiz.demo.jsonlogin.securitycomponents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 从请求中抽取token，交给后续的过滤器验证
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("进入TokenAuthenticationFilter：{}",request.getRequestURI());
        if(!SecurityUtils.isAuthenticated()){
            String headerAuth = request.getHeader("Authorization");
            String token = null;
            if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
                 token = headerAuth.substring(7, headerAuth.length());
                Authentication auth = new TokenAuthentication(token);
                log.info("token：{}",token);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            request.setAttribute(SecurityUtils.TOKEN_AUTH_FILTER_PATH+".FILTERED", true);
        }

        filterChain.doFilter(request,response);
    }
}
