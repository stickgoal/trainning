package me.maiz.shiro.shirojwt.component;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import me.maiz.shiro.shirojwt.common.JwtUtils;
import me.maiz.shiro.shirojwt.model.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        Object primaryPrincipal = principals.getPrimaryPrincipal();
        User user = (User) primaryPrincipal;
        //查询权限等

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(null);
        simpleAuthorizationInfo.setStringPermissions(null);

        return simpleAuthorizationInfo;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("登录操作：{}",token);
        System.out.println("jwt:"+token);
        JwtToken jwtToken = (JwtToken) token;
        //从token中解析出jwt附带的数据，如果jwt过期会抛出异常
        User user = JwtUtils.parseJwt(jwtToken.getToken());


        return new SimpleAuthenticationInfo(user,jwtToken.getToken(),getName());
    }

}
