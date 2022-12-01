package me.maiz.demo.jsonlogin.securitycomponents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
@Slf4j
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private TokenService tokenService;

    public TokenAuthenticationProvider(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("{}",authentication);

        if (authentication.isAuthenticated()) {
            return authentication;
        }

        String token = authentication.getCredentials().toString();
        if(Objects.isNull(token)||!StringUtils.hasText(token)){
            log.info("没有token:{}",token);
            return authentication;
        }

        UserDetails userDetails = tokenService.authenticate(token);

        Authentication auth = new PreAuthenticatedAuthenticationToken(userDetails, token, userDetails.getAuthorities());
        auth.setAuthenticated(true);

        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(TokenAuthentication.class);
    }
}
