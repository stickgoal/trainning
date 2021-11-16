package me.maiz.shiro.shirojwt.config;

import me.maiz.shiro.shirojwt.component.JWTFilter;
import me.maiz.shiro.shirojwt.component.ShiroRealm;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import javax.servlet.Filter;
import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {


    /**
     * 自定义Realm 提供数据源
     * @return
     */
    @Bean
    public Realm shiroRealm(){
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCacheManager(cacheManager());
        return shiroRealm;
    }


    @Bean
    public MemoryConstrainedCacheManager cacheManager(){
        return new MemoryConstrainedCacheManager();
    }



    /**
     * shiro的过滤器链
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        DefaultShiroFilterChainDefinition sfcd = new DefaultShiroFilterChainDefinition();

        // 在 Shiro过滤器链上加入 自定义过滤器JWTFilter 并取名为jwt
        LinkedHashMap<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new JWTFilter());
        shiroFilterFactoryBean.setFilters(filters);

        //放行 使用
        sfcd.addPathDefinition("/","anon");
        sfcd.addPathDefinition("/login","anon");
        sfcd.addPathDefinition("/error/404.html","anon");
        sfcd.addPathDefinition("/css/**","anon");
        sfcd.addPathDefinition("/js/**","anon");
        sfcd.addPathDefinition("/images/**","anon");
        sfcd.addPathDefinition("/fonts/**","anon");
        sfcd.addPathDefinition("/html/**","anon");
        //logout 登出
        sfcd.addPathDefinition("/logout","logout");
        //其他则需要认证
        sfcd.addPathDefinition("/**","jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(sfcd.getFilterChainMap());

        return shiroFilterFactoryBean;
    }



}
