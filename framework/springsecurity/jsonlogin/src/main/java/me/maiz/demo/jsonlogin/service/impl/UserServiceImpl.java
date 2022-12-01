package me.maiz.demo.jsonlogin.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.maiz.demo.jsonlogin.service.UserService;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("查询用户名：{}",username);

        Collection<? extends GrantedAuthority> authorites = new ArrayList<>();

        return new User(username,"abc123", authorites);
    }
}
