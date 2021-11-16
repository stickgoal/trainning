package com.woniuxy.boot.ssmboot.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.woniuxy.boot.ssmboot.common.EncryptUtil;
import com.woniuxy.boot.ssmboot.component.MybatisShiroRealm;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 提供自定义Realm+定义shiroFilter
 */
@Configuration
public class ShiroConfig {
    /**
     * 凭证匹配器
     * @return
     */
    @Bean
    public CredentialsMatcher credentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //hash算法的算法名
        hashedCredentialsMatcher.setHashAlgorithmName(EncryptUtil.HASH_ALGORITHM_NAME);
        //hash迭代次数
        hashedCredentialsMatcher.setHashIterations(EncryptUtil.HASH_ITERATIONS);
        return hashedCredentialsMatcher;
    }

    @Bean
    public CacheManager cacheManager(){
        return new MemoryConstrainedCacheManager();
    }


    @Bean
    public Realm shiroRealm(){
        MybatisShiroRealm mybatisShiroRealm = new MybatisShiroRealm();
        //设置密码匹配器，用于密码的匹配
        mybatisShiroRealm.setCredentialsMatcher(credentialsMatcher());
        return mybatisShiroRealm;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition(){
        DefaultShiroFilterChainDefinition sfcd= new DefaultShiroFilterChainDefinition();
        //anon 匿名访问，不需要登录
        //authc 登录请求
        //logout 登出请求
        //user  需要用户登录
        sfcd.addPathDefinition("/css/**","anon");
        sfcd.addPathDefinition("/js/**","anon");
        sfcd.addPathDefinition("/images/**","anon");
        sfcd.addPathDefinition("/register","anon");
        sfcd.addPathDefinition("/login","anon");
        sfcd.addPathDefinition("/error","anon");
        //放行Swagger2页面，需要放行这些
        sfcd.addPathDefinition("/swagger-ui.html","anon");
        sfcd.addPathDefinition("/swagger/**","anon");
        sfcd.addPathDefinition("/webjars/**", "anon");
        sfcd.addPathDefinition("/swagger-resources/**","anon");
        sfcd.addPathDefinition("/v2/**","anon");
        sfcd.addPathDefinition("/static/**", "anon");
        
//        sfcd.addPathDefinition("/","anon");
        sfcd.addPathDefinition("/logout","logout");
        sfcd.addPathDefinition("/**","user");

        return sfcd;
    }

    /**
     * 支持 thymeleaf的shiro方言
     * @return
     */
    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }

}
