package me.maiz.framework.shiro.shiroboot.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import me.maiz.framework.shiro.shiroboot.dao.PermRepo;
import me.maiz.framework.shiro.shiroboot.dao.RoleRepo;
import me.maiz.framework.shiro.shiroboot.dao.UserRepository;
import me.maiz.framework.shiro.shiroboot.shiro.DbRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ShiroConfig {
    /**
     * 配置自定义 realm
     *
     * @return
     */
    @Bean
    public Realm realm(UserRepository userRepository, RoleRepo roleRepo, PermRepo permRepo) {
        DbRealm loginRealm = new DbRealm();
//        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
//        credentialsMatcher.setHashIterations(3);
//        credentialsMatcher.setHashAlgorithmName("md5");
//        loginRealm.setCredentialsMatcher(credentialsMatcher);
        loginRealm.setUserRepository(userRepository);
        loginRealm.setRoleRepo(roleRepo);
        loginRealm.setPermRepo(permRepo);
        return loginRealm;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition shiroFilterChainDefinition = new DefaultShiroFilterChainDefinition();
        shiroFilterChainDefinition.addPathDefinition("/lib/**", "anon");
        shiroFilterChainDefinition.addPathDefinition("/static/**", "anon");
        shiroFilterChainDefinition.addPathDefinition("/logout", "logout");
        shiroFilterChainDefinition.addPathDefinition("/login", "authc");
        shiroFilterChainDefinition.addPathDefinition("/**", "user");
        return shiroFilterChainDefinition;
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}