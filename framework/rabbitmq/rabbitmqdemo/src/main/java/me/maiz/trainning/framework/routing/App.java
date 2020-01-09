package me.maiz.trainning.framework.routing;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {

    public static void main(String[] args) throws IOException, TimeoutException {
        new LogConsumerDirect().consume(1,new String[]{"ERROR"});
        new LogConsumerDirect().consume(2,new String[]{"WARN","INFO"});
    }

}
