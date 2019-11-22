package com.woniuxy.boot.ssmboot.component;

import com.woniuxy.boot.ssmboot.dao.PermMapper;
import com.woniuxy.boot.ssmboot.dao.RoleMapper;
import com.woniuxy.boot.ssmboot.dao.UserMapper;
import com.woniuxy.boot.ssmboot.entity.Role;
import com.woniuxy.boot.ssmboot.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MybatisShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermMapper permMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        //查询所有角色
        Set<Role> roles = roleMapper.findRolesByUserId(user.getUserId());
        //提取角色的roleValue
        Set<String> roleStrings = roles.stream().map(Role::getRoleValue).collect(Collectors.toSet());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roleStrings);

        //根据角色ID查询权限
        Set<Integer> roleIds = roles.stream().map(Role::getRoleId).collect(Collectors.toSet());
        Set<String> permissions = permMapper.findPermInRoleIds(roleIds);

        info.setStringPermissions(permissions);

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("获取认证信息"+token);

        String username = (String) token.getPrincipal();

       User user= userMapper.findByUsername(username);
        if(user==null){
            throw new UnknownAccountException("用户["+username+"]不存在");
        }

        return new SimpleAuthenticationInfo(user,user.getPassword().toCharArray(),getName());
    }
}
