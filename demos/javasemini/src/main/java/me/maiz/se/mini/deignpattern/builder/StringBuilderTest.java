package me.maiz.se.mini.deignpattern.builder;

public class StringBuilderTest {
    public static void main(String[] args) {
        StringBuilder strBuilder = new StringBuilder();
        String result = strBuilder.append("something magic")
                .append("is")
                .append("happening")
                .toString();
    }
}
