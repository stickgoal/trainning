package me.maiz.demo.jsonlogin.securitycomponents.impl;

import io.jsonwebtoken.Claims;
import me.maiz.demo.jsonlogin.mapper.UserMapper;
import me.maiz.demo.jsonlogin.securitycomponents.JwtHelper;
import me.maiz.demo.jsonlogin.securitycomponents.TokenService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;

@Component
public class TokenServiceImpl implements TokenService {

    @Resource
    private JwtHelper jwtHelper;

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails authenticate(String token) {

        Claims claims = jwtHelper.parseJWT(token);

        String username = (String) claims.get("username");


        return User.builder()
                .username(username)
                .password("")
                .roles("API")
                .authorities(AuthorityUtils.createAuthorityList("AAA"))
                .build();
    }
}
