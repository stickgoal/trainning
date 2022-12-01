package me.maiz.demo.jsonlogin.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    private boolean success;

    private String code;

    private String message;

    private Object data;

    public static Result success(Object data) {
        return new ResultBuilder().success(true).code("SUCCESS").message("执行成功").data(data).build();
    }

    public static Result fail(String message){
        return fail("FAIL",message);
    }
    public static Result fail(String code,String message){
        return new ResultBuilder().success(false).code(code).message(message).build();
    }
}
