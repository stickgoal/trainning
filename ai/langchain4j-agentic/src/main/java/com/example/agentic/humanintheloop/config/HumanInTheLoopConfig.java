package com.example.agentic.humanintheloop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HumanInTheLoop 配置。
 *
 * <p>本实现采用 LangChain4j 官方默认的 {@code @HumanInTheLoop} + {@code PendingResponse}
 * 机制，工作流线程会在审批点通过 {@code PendingResponse.blockingGet()} <b>阻塞</b>等待人工审批，
 * 这是该机制的固有语义（与上一版"非阻塞状态机"是两条不同路线）。
 * 因此需要一个线程池来承载这些"阻塞等待"的工作流线程。</p>
 */
@Configuration
public class HumanInTheLoopConfig {

    /**
     * 承载阻塞式工作流线程的线程池。
     * 每个在途审批会占用一个线程直到人工完成审批；生产环境应根据并发审批量调大或改用虚拟线程。
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService hitlWorkflowExecutor() {
        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "hitl-workflow-" + counter.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        };
        return Executors.newFixedThreadPool(16, factory);
    }
}
