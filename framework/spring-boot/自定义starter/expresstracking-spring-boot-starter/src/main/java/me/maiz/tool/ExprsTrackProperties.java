package me.maiz.tool;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tool.exprstrack")
public class ExprsTrackProperties {

    private String userKey;

    private String userSecret;

    private String apiUrl;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserSecret() {
        return userSecret;
    }

    public void setUserSecret(String userSecret) {
        this.userSecret = userSecret;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public String toString() {
        return "ExprsTrackProperties{" +
                "userKey='" + userKey + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                '}';
    }
}
