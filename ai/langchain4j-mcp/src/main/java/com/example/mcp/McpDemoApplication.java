package com.example.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LangChain4j MCP Tutorial Demo Application.
 *
 * Demonstrates Model Context Protocol (MCP) integration with LangChain4j:
 * - stdio transport
 * - HTTP/SSE transport (legacy)
 * - Streamable HTTP transport
 * - Multiple MCP providers
 * - AI Service + MCP Tool Provider
 */
@SpringBootApplication
public class McpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpDemoApplication.class, args);
    }
}
