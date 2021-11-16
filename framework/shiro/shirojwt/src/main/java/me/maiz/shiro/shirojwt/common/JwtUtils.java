package me.maiz.shiro.shirojwt.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.maiz.shiro.shirojwt.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    public static final String AUTH_TOKEN_NAME="auth_token";

    /**
     * 签名用的密钥
     */
    private static final String SIGNING_KEY = "78sebr72umyz33i9876gc31urjgyfhgj";

    private static final long DEFAULT_EXPIRATION_MILLIS=2*24*60*60*1000L;
    private static final String USER_ID_KEY = "userId";
    public static final String USERNAME_KEY = "username";


    public static String createJWT(User user) {
        Map<String,Object> claims = new HashMap<>();
        claims.put(USER_ID_KEY,user.getUserId());
        claims.put(USERNAME_KEY,user.getUsername());
        return create(DEFAULT_EXPIRATION_MILLIS,claims);
    }

    public static User parseJwt(String token){
        Claims claims = parse(token);
        User user = new User();
        user.setUserId((Integer) claims.get(USER_ID_KEY));
        user.setUsername((String) claims.get(USERNAME_KEY));
        return user;
    }
    /**
     * 用户登录成功后生成Jwt
     * 使用Hs256算法
     *
     * @param expiredInMillis jwt过期时间
     * @param claims 保存在Payload（有效载荷）中的内容
     * @return token字符串
     */
    public static String create(long expiredInMillis, Map<String, Object> claims) {
        //指定签名的时候使用的签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //创建一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //保存在Payload（有效载荷）中的内容
                .setClaims(claims)
                //iat: jwt的签发时间
                .setIssuedAt(now)
                //设置过期时间
                .setExpiration(new Date(nowMillis+expiredInMillis))
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, SIGNING_KEY);

        return builder.compact();
    }

    /**
     * 解析token，获取到Payload（有效载荷）中的内容，包括验证签名，判断是否过期
     *
     * @param token
     * @return
     */
    public static Claims parse(String token) {
        //得到DefaultJwtParser
        Claims claims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(SIGNING_KEY)
                //设置需要解析的token
                .parseClaimsJws(token).getBody();
        return claims;
    }
}
