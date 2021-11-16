package me.maiz.project.eduk15boss.components;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.shiro.authc.AuthenticationToken;


@Data
@Accessors(chain = true)
public class JwtToken implements AuthenticationToken {


    /**
     * 登录token
     */
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }


}