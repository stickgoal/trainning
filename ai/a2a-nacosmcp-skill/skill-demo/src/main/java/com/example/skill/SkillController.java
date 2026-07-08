package com.example.skill;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Skill 控制器
 * 功能点：通过 REST API 演示技能系统的渐进式披露机制
 * 
 * 工作流程：
 * 1. 系统提示中只注入技能摘要（名称+描述）
 * 2. 模型判断需要某技能时，调用 read_skill(skill_name) 加载完整 SKILL.md
 * 3. 按技能指导完成用户任务
 */
@RestController
@RequestMapping("/api/skill")
public class SkillController {

    private final ReactAgent skillAgent;
    private final SkillRegistry skillRegistry;

    public SkillController(ReactAgent skillAgent, SkillRegistry skillRegistry) {
        this.skillAgent = skillAgent;
        this.skillRegistry = skillRegistry;
    }

    /**
     * 列出所有可用技能
     * 示例：GET /api/skill/list
     */
    @GetMapping("/list")
    public String listSkills() {
        var skills = skillRegistry.listAll();
        StringBuilder sb = new StringBuilder("【可用技能列表】\n\n");
        for (var skill : skills) {
            sb.append("• ").append(skill.name())
              .append("：").append(skill.description()).append("\n");
        }
        sb.append("\n共 ").append(skills.size()).append(" 个技能");
        return sb.toString();
    }

    /**
     * 文案写作演示
     * 示例：GET /api/skill/copywriting?product=智能保温杯，350ml，不锈钢材质，12小时保温，适合办公室使用
     * 
     * 功能点：模型会先调用 read_skill("copywriting") 加载技能，
     * 然后按照技能指导生成文案
     */
    @GetMapping("/copywriting")
    public String copywriting(@RequestParam String product) {
        Optional<com.alibaba.cloud.ai.graph.OverAllState> result =
                skillAgent.invoke("请为以下商品撰写营销文案：" + product);
        return result.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("文案生成失败");
    }

    /**
     * 选品分析演示
     * 示例：GET /api/skill/selection?target=25-35岁女性，月消费3000-5000元，关注时尚和健康
     * 
     * 功能点：模型会先调用 read_skill("product-selection") 加载技能，
     * 然后按照技能指导进行选品分析
     */
    @GetMapping("/selection")
    public String productSelection(@RequestParam String target) {
        Optional<com.alibaba.cloud.ai.graph.OverAllState> result =
                skillAgent.invoke("请根据以下目标人群进行选品分析：" + target);
        return result.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("选品分析失败");
    }

    /**
     * 代码审查演示
     * 示例：GET /api/skill/review?code=public class User { private String name; public String getName() { return name; } }
     * 
     * 功能点：模型会先调用 read_skill("code-reviewer") 加载技能，
     * 然后按照技能指导进行代码审查，生成审查报告
     */
    @GetMapping("/review")
    public String codeReview(@RequestParam String code) {
        Optional<com.alibaba.cloud.ai.graph.OverAllState> result =
                skillAgent.invoke("请审查以下 Java 代码：\n```java\n" + code + "\n```");
        return result.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("代码审查失败");
    }

    /**
     * 技能发现演示
     * 示例：GET /api/skill/discover
     * 
     * 功能点：让 Agent 自己介绍自己拥有哪些技能，
     * 验证技能加载是否成功
     */
    @GetMapping("/discover")
    public String discoverSkills() {
        Optional<com.alibaba.cloud.ai.graph.OverAllState> result =
                skillAgent.invoke("请介绍一下你目前拥有哪些技能，以及每个技能可以做什么");
        return result.map(state -> {
            Object value = state.value("messages");
            return value != null ? value.toString() : "无返回结果";
        }).orElse("技能发现失败");
    }
}