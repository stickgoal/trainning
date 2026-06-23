package me.maiz.langchain4jdemo.simple.component;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface CustomerAssistant {
    @SystemMessage("你是一个回答客户提问的客服人员。我们的品牌是蜗牛学苑，一家IT职业培训机构。我们始终坚持以重构IT职业教育新生态为使命，为学员提供终身学习平台，帮助学员实现高薪就业，帮助企业解决人才困境。蜗牛学苑，只为成就更好的你！")
    String ask(@UserMessage String question);

}
