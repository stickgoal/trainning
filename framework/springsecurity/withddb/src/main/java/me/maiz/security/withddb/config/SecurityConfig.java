package me.maiz.security.withddb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

//继承配置类适配器
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 密码加密器，这里的实现是无操作
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Resource
    private UserDetailsService userDetailsService;

    //配置认证数据
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());

    }


    //配置基于http认证相关
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated() //任何已认证用户都可以访问
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home.html")      //如果用户没有访问任何地址，则跳转到defaultSuccessUrl否则跳转到用户之前访问的
                //.successForwardUrl("/home.html")   // 登录后直接跳转到该地址，defaultSuccessUrl和successForwardUrl二选一
                .failureUrl("/loginFail.html") //登录失败地址
                .permitAll() //允许以上相关地址被所有用户访问，
                .and()
                .csrf()
                .disable();//禁用csrf
    }
}
