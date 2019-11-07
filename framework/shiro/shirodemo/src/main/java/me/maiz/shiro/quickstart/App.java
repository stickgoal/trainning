package me.maiz.shiro.quickstart;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();

        SecurityUtils.setSecurityManager(securityManager);

        Subject currUser = SecurityUtils.getSubject();

        UsernamePasswordToken upt = new UsernamePasswordToken("wanger","123456");
        UsernamePasswordToken upt2 = new UsernamePasswordToken("zhangsan","654321");
        try {
            currUser.login(upt2);
            System.out.println("登录成功");
        }catch (AuthenticationException e){
            System.out.println("登录失败："+e.getMessage());
        }
        Object principal = currUser.getPrincipal();
        System.out.println(principal);

        System.out.println(currUser.hasRole("manager"));
        System.out.println(currUser.hasRole("clerk"));
        System.out.println(currUser.isPermitted("user:create"));
        System.out.println(currUser.isPermitted("user:delete"));
        System.out.println(currUser.isPermitted("user:read"));

    }
}
