package me.maiz.app.dailycost.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Lucas on 2017-03-08.
 */
public class WebUtil {

    private static Logger logger = LoggerFactory.getLogger(WebUtil.class);

    public static String redirectWithMessage(String msgKey,HttpServletRequest request, String uri, String message){
        try {
            return "redirect:" + uri + "?" +msgKey+ "=" + URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            logger.error("系统异常，不支持的编码集",e1);
            request.setAttribute(Constants.MSG_KEY,"系统异常，不支持的编码集");
        }
        return Constants.DEFAULT_ERROR_PAGE;
    }
}
