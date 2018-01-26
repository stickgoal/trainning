package me.maiz.app.dailycost.components;

import me.maiz.app.dailycost.common.Constants;
import me.maiz.app.dailycost.common.WebUtil;
import me.maiz.app.dailycost.enums.DailyCostResultCode;
import me.maiz.app.dailycost.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 全局异常处理，将系统异常和业务异常区分处理
 * Created by Lucas on 2017-02-21.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String DEFAULT_ERROR_PAGE = "error";

    @ExceptionHandler(Exception.class)
    public String logException(Exception e, HttpServletRequest request){
        logger.error("系统异常被捕获",e);
        request.setAttribute(Constants.ERROR_MSG_KEY,e.getMessage());
        return DEFAULT_ERROR_PAGE;
    }

    @ExceptionHandler
    public String processAppException(AppException e ,HttpServletRequest request){
        logger.info("业务处理异常：{}，{}",e.getResultCode(),e.getMessage());
        if(e.getResultCode()== DailyCostResultCode.INVALID_ARGUMENT){
            String requestURI = request.getRequestURI();
            //使用get方法访问页面地址
            return WebUtil.redirectWithMessage(Constants.ERROR_MSG_KEY,request,requestURI,"参数不合法（" + e.getMessage() + ")");
        }

        if(e.getResultCode()== DailyCostResultCode.USER_NOT_FOUND_OR_PASSWORD_ERROR){
            request.setAttribute(Constants.ERROR_MSG_KEY,DailyCostResultCode.USER_NOT_FOUND_OR_PASSWORD_ERROR.getMessage());
            return "forward:/index.jsp";
        }
        return DEFAULT_ERROR_PAGE;
    }



}
