package me.maiz.framework.shiro.shiroboot.shiro;

import me.maiz.framework.shiro.shiroboot.dao.PermRepo;
import me.maiz.framework.shiro.shiroboot.dao.RoleRepo;
import me.maiz.framework.shiro.shiroboot.dao.UserRepository;
import me.maiz.framework.shiro.shiroboot.entity.Permission;
import me.maiz.framework.shiro.shiroboot.entity.Role;
import me.maiz.framework.shiro.shiroboot.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

public class DbRealm extends AuthorizingRealm {
    private Logger logger = LoggerFactory.getLogger(DbRealm.class);

    private UserRepository userRepository;

    private RoleRepo roleRepo;

    private PermRepo permRepo;

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authzInfo = new SimpleAuthorizationInfo();
        Set<Role> roleSet = roleRepo.findByUserId(user.getUserId());
        Set<String> stringRoles = roleSet.stream().map(Role::getRoleValue).collect(Collectors.toSet());
        authzInfo.setRoles(stringRoles);
        logger.info("提取角色信息，{}",roleSet);


        Set<Permission> permissionSet = permRepo.findByUserId(user.getUserId());
        Set<String> stringPermissions = permissionSet.stream().map(Permission::getPermValue).collect(Collectors.toSet());
        logger.info("提取权限信息，{}",stringPermissions);

        authzInfo.setStringPermissions(stringPermissions);
        return authzInfo;
    }

    /**
     * 验证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        User user = userRepository.findByUsername(username);
//        User user = new User();
//        user.setUsername("wanger");
//        user.setPassword("123");
        if(user==null){
            throw new UnknownAccountException();
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user,user.getPassword(),getName());

        return simpleAuthenticationInfo;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setRoleRepo(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public void setPermRepo(PermRepo permRepo) {
        this.permRepo = permRepo;
    }
}
