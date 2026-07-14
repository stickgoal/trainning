package com.example.agentic.humanintheloop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * HumanInTheLoop 生产化配置。
 *
 * <h3>核心思路</h3>
 * 把「人工等待」从线程里彻底拿掉：HTTP 请求线程只在有界的 LLM 调用（前置检查 / 执行）上停留，
 * 而人工审批是「落库 + 立即返回」的异步动作。真正需要线程的仅限执行阶段的 LLM 调用，
 * 由下方专用线程池承载，且具备有界队列与背压策略。
 *
 * <p>对比原实现：原代码用 {@code Executors.newCachedThreadPool()} 跑整条工作流，
 * 并在 {@code PendingResponse.blockingGet()} 处把线程一直挂起直到人工审批，
 * 再用 {@code future.get(120s)} 把 HTTP 线程也阻塞住 —— 既浪费线程又无法重启恢复。
 */
@Configuration
@EnableAsync
@EnableScheduling
public class HumanInTheLoopConfig {

    /** 执行 Agent（LLM 调用）专用线程池；仅承载有限的执行阶段，绝不承载人工等待。 */
    @Bean("humanApprovalTaskExecutor")
    public Executor humanApprovalTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("hitl-exec-");
        // 队列满时由调用方线程直接执行，形成自然背压，避免任务被丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
