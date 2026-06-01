package me.maiz.ai.helloworld.entity;

import lombok.Data;
import me.maiz.ai.helloworld.param.ChatParam;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 马宇航
 * @Description: TODO
 * @DateTime: 26/02/06/星期五 12:14
 * @Component: 成都蜗牛学苑
 **/
@Data
public class AiMessagesDTO implements Serializable{
    private List<ChatParam> messages;
}
