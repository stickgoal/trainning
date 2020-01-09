package me.maiz.trainning.framework.topics;

import me.maiz.trainning.framework.routing.LogConsumerDirect;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class App {

    public static void main(String[] args) throws IOException, TimeoutException {
        //收集所有兔子的信息
        new LogConsumerTopic().consume("*.*.rabbit\t==>",new String[]{"*.*.rabbit"});
        //收集所有orange的动物信息
        new LogConsumerTopic().consume("*.orange.*\t==>",new String[]{"*.orange.*"});
        //收集所有lazy的动物信息
        new LogConsumerTopic().consume("lazy.#\t==>",new String[]{"lazy.#"});
    }

}
