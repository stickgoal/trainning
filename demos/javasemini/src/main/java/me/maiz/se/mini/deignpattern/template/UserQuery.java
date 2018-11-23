package me.maiz.se.mini.deignpattern.template;

public class UserQuery extends QueryTemplate {
    @Override
    protected void checkArguments(String param1) {
        System.out.println("userQuery ： 检查参数");
    }
    @Override
    protected Result getResult() {
        System.out.println("userQuery ： 得到结果");
        return null;
    }
    @Override
    protected void execute(String param1) {
        System.out.println("userQuery ： 执行查询");
    }
}
