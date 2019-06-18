package me.maiz.se.mini.deignpattern.template;

public class TemplateApp {
    public static void main(String[] args) {
        QueryTemplate qt = new ProductQuery();
        qt.query("aaa");
        QueryTemplate qt2 = new UserQuery();
        qt2.query("aaa");
    }
}
