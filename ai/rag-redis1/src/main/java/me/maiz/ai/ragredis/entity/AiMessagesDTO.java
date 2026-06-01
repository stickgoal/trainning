package me.maiz.ai.ragredis.entity;

import lombok.Data;
import me.maiz.ai.ragredis.param.ChatParam;

import java.io.Serializable;
import java.util.List;

@Data
public class AiMessagesDTO implements Serializable{
    private List<ChatParam> messages;
}
