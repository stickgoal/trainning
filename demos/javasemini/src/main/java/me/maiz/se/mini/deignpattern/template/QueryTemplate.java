package me.maiz.se.mini.deignpattern.template;

public abstract class QueryTemplate {

    public Result query(String param1) {
        //打印日志
        System.out.println("queryTemplate : 查询开始，参数为:"+param1);
        try {
            //检查参数,可能抛出参数异常
            checkArguments(param1);
            //执行查询
            execute(param1);
            //封装返回结果
             return getResult();
        }catch (Exception e){
            //日志统一处理
            e.printStackTrace();
        }

        return null;
    }

    protected abstract void checkArguments(String param1);

    protected abstract Result getResult();

    protected abstract void execute(String param1);
}
