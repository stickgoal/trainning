package me.maiz.demo.jsonlogin.securitycomponents;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

    UserDetails authenticate(String token);

}
