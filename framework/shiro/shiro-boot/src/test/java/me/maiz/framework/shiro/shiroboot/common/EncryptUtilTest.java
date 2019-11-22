package me.maiz.framework.shiro.shiroboot.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptUtilTest {

    @Test
    void encrypt() {
        System.out.println(EncryptUtil.encrypt("abc123"));
    }
}