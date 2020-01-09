package me.maiz.trainning.framework.publishsubscribe;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {

    public static void main(String[] args) throws IOException, TimeoutException {
        new LogConsumer().consume(1);
        new LogConsumer().consume(2);
        new LogConsumer().consume(3);
    }

}
