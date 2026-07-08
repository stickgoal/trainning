package com.example.skill;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.skills.SkillsAgentHook;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Skill 配置类
 * 功能点：
 * 1. 使用 ClasspathSkillRegistry 从 classpath:skills 加载技能
 * 2. 通过 SkillsAgentHook 将技能注入 Agent
 * 3. 实现渐进式披露：初始只注入技能摘要，模型按需调用 read_skill 加载完整内容
 */
@Configuration
public class SkillConfig {

    /**
     * 技能注册中心
     * 功能点：从 classpath 的 skills 目录自动扫描并加载所有技能
     */
    @Bean
    public SkillRegistry skillRegistry() {
        return ClasspathSkillRegistry.builder()
                .classpathPath("skills")
                .autoLoad(true)
                .build();
    }

    /**
     * 技能钩子
     * 功能点：将 SkillRegistry 注入 Agent，自动注册 read_skill 工具
     */
    @Bean
    public SkillsAgentHook skillsAgentHook(SkillRegistry skillRegistry) {
        return SkillsAgentHook.builder()
                .skillRegistry(skillRegistry)
                .autoReload(true)
                .build();
    }

    /**
     * 技能 Agent
     * 功能点：创建具备技能能力的 ReactAgent
     * 通过 SkillsAgentHook 注入技能系统，Agent 可以：
     * 1. 查看可用技能列表
     * 2. 按需调用 read_skill 加载技能详情
     * 3. 使用技能提供的工具和脚本
     */
    @Bean
    public ReactAgent skillAgent(ChatModel chatModel, SkillsAgentHook skillsAgentHook) {
        return ReactAgent.builder()
                .name("skill_agent")
                .description("具备技能系统的智能体，支持文案写作、选品分析和代码审查")
                .model(chatModel)
                .saver(new InMemoryChatMemory())
                .hooks(List.of(skillsAgentHook))
                .enableLogging(true)
                .build();
    }
}