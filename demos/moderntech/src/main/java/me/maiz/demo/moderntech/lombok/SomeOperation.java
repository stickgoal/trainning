package me.maiz.demo.moderntech.lombok;

import lombok.SneakyThrows;

import java.sql.SQLException;

public class SomeOperation {

    @SneakyThrows(RuntimeException.class)
    public String  doSomethingDangerous(){
        return "x";
    }


}
