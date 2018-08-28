package me.maiz.demo.moderntech.lombok;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
public class SomeOperation {

    @SneakyThrows(RuntimeException.class)
    public String  doSomethingDangerous(){
        return "x";
    }


}
