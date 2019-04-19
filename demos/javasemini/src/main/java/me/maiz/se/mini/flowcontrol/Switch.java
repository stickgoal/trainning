package me.maiz.se.mini.flowcontrol;

public class Switch {
    public static void main(String[] args) {
        String country = "Japan";

        String greeting;

        switch (country) {
            case "china":
                greeting = "你好！";
                break;
            case "USA":
                greeting = "Hello";
                break;
            case "Japan":
                greeting = "こんにちは";
                break;
            case "France":
                greeting = "bonjour";
                break;
            default:
                greeting = "$#@$";
        }

        System.out.println(greeting);
    }
}
