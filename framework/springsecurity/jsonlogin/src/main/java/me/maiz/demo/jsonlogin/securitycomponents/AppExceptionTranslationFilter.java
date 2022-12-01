package me.maiz.demo.jsonlogin.securitycomponents;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.maiz.demo.jsonlogin.common.AppException;
import me.maiz.demo.jsonlogin.common.Result;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * There might be ResultExceptions during authentication, this filter will transfer the exceptions
 * into 200 with JSON body explaining why.
 * <p>
 * Mainly used for Token Authentication.
 */

public class AppExceptionTranslationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        try {
            fc.doFilter(request, response);
        } catch (AppException ex) {
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(new ObjectMapper().writeValueAsString(failResult(ex)));
            response.getWriter().flush();
        }
    }

    private Result failResult(AppException ex) {
        return Result.builder().code("403").message(ex.getMessage()).success(false).build();
    }
}
