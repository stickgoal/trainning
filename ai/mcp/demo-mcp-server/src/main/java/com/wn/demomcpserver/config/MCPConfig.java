package com.wn.demomcpserver.config;

import com.wn.demomcpserver.tools.product.ProductTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MCPConfig {
    @Bean
    public ToolCallbackProvider toolCallbackProvider(ProductTools productTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(productTools) // 向MCP-Client暴露服务
                .build();
    }
}
