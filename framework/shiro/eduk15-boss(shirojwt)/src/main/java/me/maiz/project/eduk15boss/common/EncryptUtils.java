package me.maiz.project.eduk15boss.common;

import org.apache.shiro.crypto.hash.SimpleHash;

public class EncryptUtils {

    public static  final  String ALGORITHM_NAME="SHA-256";
    public static  final  int HASH_ITERATIONS=1;

    public static String encrypt(String password){
        SimpleHash simpleHash = new SimpleHash(ALGORITHM_NAME,password);
        simpleHash.setIterations(HASH_ITERATIONS);
        return simpleHash.toHex();
    }

}
