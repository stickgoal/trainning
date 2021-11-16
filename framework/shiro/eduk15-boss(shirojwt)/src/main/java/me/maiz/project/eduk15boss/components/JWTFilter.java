package me.maiz.project.eduk15boss.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import me.maiz.project.eduk15boss.common.JwtUtils;
import me.maiz.project.eduk15boss.common.Result;
import me.maiz.project.eduk15boss.model.Operator;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTFilter extends AuthenticatingFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //全局允许跨域
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();

        logger.info("进入预处理:{}-{}", httpServletRequest.getMethod(), requestURI);

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个 option请求，这里我们给 option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }


        return super.preHandle(request, response);

    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        logger.info("进入isAccessAllowed");

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            AuthenticationToken token = createToken(request, response);
            Subject subject = getSubject(request, response);
            subject.login(token);
            return super.isAccessAllowed(request, response, mappedValue);
        } catch (AuthenticationException e) {
            if (e.getCause() instanceof JwtException) {
                logger.warn("jwt异常", e);
                fail(Result.fail("JWT_NOT_CORRECT"), httpServletResponse);
                return false;
            }
            logger.warn("认证异常", e);
            fail(Result.fail("NOT_LOGIN"), httpServletResponse);
        } catch (AuthorizationException e) {
            logger.warn("授权异常", e);
            fail(Result.fail("NO_PERMISSION"), httpServletResponse);

        } catch (Exception e) {
            logger.warn("系统异常", e);
            fail(Result.fail("SYS_ERROR"), httpServletResponse);
        }
        return false;

    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return false;
    }


    /**
     * 从请求中提取Token
     *
     * @param req
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest req, ServletResponse response) throws Exception {
        logger.info("进入createToken");

        HttpServletRequest request = (HttpServletRequest) req;
        String token = request.getHeader(JwtUtils.AUTH_TOKEN_NAME);
        if (Strings.isNullOrEmpty(token)) {
            throw new UnknownAccountException("请求不合法，JWT token未传入");
        }

        return new JwtToken(token);
    }


    private void fail(Result result, HttpServletResponse httpServletResponse) {
        // 返回401
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 设置响应码为401或者直接输出消息
        ObjectMapper objectMapper = new ObjectMapper();
        httpServletResponse.setContentType("application/json;charset=utf-8");
        try {
            httpServletResponse.getWriter().print(objectMapper.writeValueAsString(result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
