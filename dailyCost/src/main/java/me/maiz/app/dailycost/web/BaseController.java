package me.maiz.app.dailycost.web;

import me.maiz.app.dailycost.common.Constants;
import me.maiz.app.dailycost.common.WebUtil;
import me.maiz.app.dailycost.dal.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Lucas on 2017-02-20.
 */
public abstract class BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected String redirectWithMessage(HttpServletRequest request,String uri,String message){
        return WebUtil.redirectWithMessage(Constants.MSG_KEY,request,uri,message);
    }

    protected String redirectWithErrorMessage(HttpServletRequest request,String uri,String message){
        return WebUtil.redirectWithMessage(Constants.ERROR_MSG_KEY,request,uri,message);
    }

    protected int getUserId(HttpSession session){
        User user = (User) session.getAttribute(Constants.USER_SESSION_KEY);
        return user.getUserId();
    }

}
