package me.maiz.security.quickstart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                httpBasic().disable()
                //表单登录处理
                .formLogin()
                .loginPage("/login.html")    //登录页位置
                .loginProcessingUrl("/login")//登录请求处理地址
                .defaultSuccessUrl("/home.html")
                .and().
                authorizeRequests()
                .antMatchers("/","/login","/login.html", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest()
                .authenticated();
//                .and()
//                .exceptionHandling();
//        http.csrf().disable() //禁用跨站csrf攻击防御，后面的章节会专门讲解
//                .formLogin()
//                .loginPage("/login.html")//用户未登录时，访问任何资源都转跳到该路径，即登录页面
//                .loginProcessingUrl("/login")//登录表单form中action的地址，也就是处理认证请求的路径
//                .defaultSuccessUrl("/index")//登录认证成功后默认转跳的路径
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login.html","/login").permitAll()//不需要通过登录验证就可以被访问的资源路径
//                .antMatchers("/biz1").hasAnyAuthority("biz1")  //前面是资源的访问路径、后面是资源的名称或者叫资源ID
//                .antMatchers("/biz2").hasAnyAuthority("biz2")
//                .antMatchers("/syslog").hasAnyAuthority("syslog")
//                .antMatchers("/sysuser").hasAnyAuthority("sysuser")
//                .anyRequest().authenticated();
    }


    @Override
    public void configure(WebSecurity web) {
        //将项目中静态资源路径开放出来
        web.ignoring()
                .antMatchers("/config/**", "/css/**", "/fonts/**", "/img/**", "/js/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("zhangsan")
                .password(passwordEncoder().encode("wohenshuai"))
                .roles("USER")
                .and()
                .withUser("admin").password("admin").roles("ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
