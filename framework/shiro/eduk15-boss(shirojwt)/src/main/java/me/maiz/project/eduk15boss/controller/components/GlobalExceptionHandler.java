package me.maiz.project.eduk15boss.controller.components;

import lombok.extern.slf4j.Slf4j;
import me.maiz.project.eduk15boss.common.Result;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Result processUnauthenticatedException(HttpServletRequest request,
                                                  AuthorizationException e) {
        String requestURI = request.getRequestURI();
        log.info("未授权异常:"+requestURI,e);
        return Result.fail("您没有权限访问该地址"+requestURI+"，请确认您的账户是否正确");
    }
}
