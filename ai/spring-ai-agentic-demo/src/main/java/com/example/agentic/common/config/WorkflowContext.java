package com.example.agentic.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作流上下文 — 替代 LangChain4j 的 AgenticScope。
 * <p>
 * 在工作流的各 Agent 步骤之间传递数据，通过 outputKey 存取中间结果。
 */
public class WorkflowContext {

    private final Map<String, Object> state = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        state.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        Object value = state.get(key);
        if (value == null) return defaultValue;
        return (T) value;
    }

    public String getString(String key, String defaultValue) {
        Object value = state.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public Map<String, Object> getAll() {
        return Map.copyOf(state);
    }
}
