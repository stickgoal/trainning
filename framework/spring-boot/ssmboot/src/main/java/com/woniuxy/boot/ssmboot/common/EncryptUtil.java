package com.woniuxy.boot.ssmboot.common;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 加密工具
 */
public class EncryptUtil {

    public static final String HASH_ALGORITHM_NAME = "SHA-256";
    public static final int HASH_ITERATIONS = 1;

    /**
     * 对密码进行加密
     * @param password
     * @return
     */
    public static String encrypt(String password){
        SimpleHash simpleHash = new SimpleHash(HASH_ALGORITHM_NAME,password);
        simpleHash.setIterations(HASH_ITERATIONS);
        return simpleHash.toString();
    }

}
