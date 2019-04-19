package me.maiz.se.mini.deignpattern.template;

public class ProductQuery extends QueryTemplate {
    @Override
    protected void checkArguments(String param1) {
        System.out.println("ProductQuery ： 检查参数");

    }
    @Override
    protected Result getResult() {
        System.out.println("ProductQuery ： 得到结果");
        return null;
    }
    @Override
    protected void execute(String param1) {
        System.out.println("ProductQuery ： 执行查询");

    }
}
