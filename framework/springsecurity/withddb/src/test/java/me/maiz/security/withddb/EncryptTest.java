package me.maiz.security.withddb;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class EncryptTest {

    @Test
    public void testEncrypt(){
        //$2a$10$7kYRdBnqzxX4efx2SAk.rO9P4rq.gl4Dnx5gcpDtIRT.gDtjVXoGK
        String encoded =  new BCryptPasswordEncoder().encode("wohenshuai");
        System.out.println(encoded);
    }
}
