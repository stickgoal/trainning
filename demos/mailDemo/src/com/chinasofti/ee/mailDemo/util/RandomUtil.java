package com.chinasofti.ee.mailDemo.util;

import java.util.Random;

public class RandomUtil {


    private static  final String SRC ="1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    public static String randomString(int length){
        StringBuilder codeBuilder = new StringBuilder();
        Random random = new Random();
        for (int i=0;i<length;i++){
            codeBuilder.append(SRC.charAt(random.nextInt(SRC.length())));
        }
        return codeBuilder.toString();
    }
}
