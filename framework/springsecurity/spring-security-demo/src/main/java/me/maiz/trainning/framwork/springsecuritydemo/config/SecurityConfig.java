package me.maiz.trainning.framwork.springsecuritydemo.config;

import me.maiz.trainning.framwork.springsecuritydemo.controller.LoginDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login","/error", "/index").permitAll()
                .antMatchers("/css/**", "/js/**", "/fonts/**").permitAll()  // 谁都可以访问
                .antMatchers("/users/**").hasRole("USER")   // 需要相应的角色才能访问
                .antMatchers("/admins/**").hasRole("ADMIN")   // 需要相应的角色才能访问
                .and()
                .formLogin()   //基于 Form 表单登录验证
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .failureUrl("/loginfail.html")
                .and().csrf().disable();  // 自定义登录界面
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Resource
    private UserDetailsService userDetailsService;

    /**
     * 认证信息管理
     * @param auth
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
