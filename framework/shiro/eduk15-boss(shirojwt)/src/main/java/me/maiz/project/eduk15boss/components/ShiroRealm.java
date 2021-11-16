package me.maiz.project.eduk15boss.components;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import me.maiz.project.eduk15boss.common.JwtUtils;
import me.maiz.project.eduk15boss.dao.OperatorMapper;
import me.maiz.project.eduk15boss.model.Operator;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("获取授权信息");
        Object primaryPrincipal = principals.getPrimaryPrincipal();
        Operator user = (Operator) primaryPrincipal;
        //TODO  查询权限等

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(null);
        simpleAuthorizationInfo.setStringPermissions(Sets.newHashSet("student:add"));
        return simpleAuthorizationInfo;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("登录操作：{}",token);
        JwtToken jwtToken = (JwtToken) token;
        Operator operator = JwtUtils.parseJwt(jwtToken.getToken());

        return new SimpleAuthenticationInfo(operator, jwtToken.getToken().toCharArray(),getName());
    }

}
