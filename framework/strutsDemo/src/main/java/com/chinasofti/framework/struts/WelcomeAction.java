package com.chinasofti.framework.struts;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Created by Lucas on 2017-01-20.
 */
public class WelcomeAction {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String username;

    private String password;

    public String execute(){
        logger.info("username ："+username+" password : "+password);
        if (username.equals("wanger")) {
            return "accessDenied";
        }else{
            return "success";
        }

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
