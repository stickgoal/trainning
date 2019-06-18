package com.chinasofti.ee;

import java.util.Random;

public class RandomUtils {

	private static final String SOURCE = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

	public static String randomString(int length) {
		StringBuilder rmBuilder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int randomIdx = new Random().nextInt(SOURCE.length());
			rmBuilder.append(SOURCE.charAt(randomIdx));
		}
		return rmBuilder.toString();
	}

}
