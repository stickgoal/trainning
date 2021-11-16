package com.woniuxy.boot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "expresstracking")
public class ExpressTrackingProperties {

    private String userKey;
    private String userSecret;
    private String apiUrl = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";


    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserSecret() {
        return this.userSecret;
    }

    public void setUserSecret(String userSecret) {
        this.userSecret = userSecret;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }


}
