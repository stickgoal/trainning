package me.maiz.shiro.shirojwt.component;

import lombok.Data;
import lombok.experimental.Accessors;
import me.maiz.shiro.shirojwt.common.JwtUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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