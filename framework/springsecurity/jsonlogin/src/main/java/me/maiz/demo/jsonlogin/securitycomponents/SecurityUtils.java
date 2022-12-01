package me.maiz.demo.jsonlogin.securitycomponents;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static final String TOKEN_AUTH_FILTER_PATH=TokenAuthenticationFilter.class.getName();

    public static boolean isAuthenticated(){
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication() != null && context.getAuthentication().isAuthenticated();
    }

}
