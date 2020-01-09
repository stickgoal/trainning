package me.maiz.project.forum26.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private boolean success;

    private Object data;

    private String message;

    public static Result success(Object data){
        return new Result(true,data,"执行成功");
    }

    public static Result success(){
        return success(null);
    }

    public static Result fail(String message){
        return new Result(false,null,message);
    }
}
