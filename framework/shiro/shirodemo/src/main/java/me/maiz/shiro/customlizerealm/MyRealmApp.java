package me.maiz.shiro.customlizerealm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public class MyRealmApp {

    public static void main(String[] args) {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-my.ini");
        SecurityManager securityManager = factory.getInstance();

        SecurityUtils.setSecurityManager(securityManager);

        Subject currUser = SecurityUtils.getSubject();

        UsernamePasswordToken upt = new UsernamePasswordToken("zhang","123");
        UsernamePasswordToken upt2 = new UsernamePasswordToken("zhangsan","654321");
        try {
            currUser.login(upt);
            System.out.println("登录成功");
        }catch (AuthenticationException e){
            System.out.println("登录失败："+e.getMessage());
        }
        Object principal = currUser.getPrincipal();
        System.out.println(principal);

        System.out.println(currUser.hasRole("manager"));
        System.out.println(currUser.isPermitted("fire"));
        System.out.println(currUser.isPermitted("raise"));
    }
}
