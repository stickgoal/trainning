package com.example.agentic.supervisor.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Supervisor 子 Agent 工具配置。
 * <p>
 * 在 LangChain4j 中，这些是独立的 @Agent 接口（EmotionAgent, FactAgent, SolutionAgent, NotifyAgent）。
 * 在 Spring AI 中，它们被实现为 Function Callback，供 Supervisor 的 ChatClient 通过 Function Calling 调用。
 */
@Slf4j
@Configuration
public class SupervisorAgentTools {

    @Bean
    public FunctionToolCallback emotionAnalysisTool() {
        return FunctionToolCallback.builder("emotionAnalysis", (String input) -> {
                    log.info("[SubAgent] emotionAnalysis called: {}", input);
                    return """
                        【情绪分析结果】
                        情绪强度: 高
                        主要情绪: 愤怒 + 失望
                        安抚建议: 
                        1. 首先表达诚挚歉意，承认用户的不良体验
                        2. 表示理解用户的感受，共情回应
                        3. 承诺会认真处理并给出明确时间节点
                        4. 提供专属客服通道，体现重视
                        
                        安抚话术示例:
                        "非常抱歉给您带来了不好的购物体验，我们完全理解您的心情。
                        您的问题我们已经高度重视，将在24小时内给出处理方案。
                        在此期间，您可以随时联系专属客服[电话]获取最新进展。"
                        """;
                })
                .description("分析用户情绪并生成安抚话术。参数：用户投诉内容")
                .inputType(String.class)
                .build();
    }

    @Bean
    public FunctionToolCallback factInvestigationTool() {
        return FunctionToolCallback.builder("factInvestigation", (String input) -> {
                    log.info("[SubAgent] factInvestigation called: {}", input);
                    return """
                        【事实调查结果】
                        订单状态: 已签收
                        签收时间: 3天前
                        商品类型: 蓝牙耳机
                        订单金额: ¥299.00
                        用户历史: 5次购买，0次退款，信用良好
                        质量投诉核实: 该批次产品有少量质量反馈记录
                        结论: 用户投诉有一定合理性，建议优先处理
                        """;
                })
                .description("调查订单事实和历史记录。参数：订单ID和投诉描述")
                .inputType(String.class)
                .build();
    }

    @Bean
    public FunctionToolCallback solutionDesignTool() {
        return FunctionToolCallback.builder("solutionDesign", (String input) -> {
                    log.info("[SubAgent] solutionDesign called: {}", input);
                    return """
                        【解决方案】
                        方案类型: 退款 + 补偿
                        具体措施:
                        1. 全额退款 ¥299.00
                        2. 赠送 ¥30 无门槛优惠券作为补偿
                        3. 免费升级为VIP会员（享受优先售后）
                        预计处理时间: 1-3个工作日
                        跟进方式: 短信 + APP推送通知处理进度
                        """;
                })
                .description("制定赔偿/解决方案。参数：情绪分析和事实调查的综合信息")
                .inputType(String.class)
                .build();
    }

    @Bean
    public FunctionToolCallback notificationDraftTool() {
        return FunctionToolCallback.builder("notificationDraft", (String input) -> {
                    log.info("[SubAgent] notificationDraft called: {}", input);
                    return """
                        【客户通知文案】
                        尊敬的用户您好：
                        
                        关于您反馈的商品质量问题，我们深表歉意。经核实，您的投诉属实，
                        我们已为您安排以下处理方案：
                        
                        1. 全额退款 ¥299.00，预计1-3个工作日到账
                        2. 赠送 ¥30 无门槛优惠券，已发放至您的账户
                        3. 您的会员等级已免费升级为VIP，享受优先售后服务
                        
                        感谢您的理解与支持，我们将持续改进产品质量和服务水平。
                        如有任何疑问，请随时联系专属客服热线：400-XXX-XXXX。
                        
                        祝您生活愉快！
                        """;
                })
                .description("生成客户通知文案。参数：解决方案详情")
                .inputType(String.class)
                .build();
    }
}
