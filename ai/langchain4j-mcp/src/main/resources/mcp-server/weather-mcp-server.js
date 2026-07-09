#!/usr/bin/env node

/**
 * Weather MCP Server - LangChain4j MCP Tutorial
 *
 * 提供两个工具：
 * 1. get_weather - 查询城市天气
 * 2. calculate - 数学计算
 *
 * 使用 @modelcontextprotocol/sdk 通过 stdio 传输通信
 */

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
    CallToolRequestSchema,
    ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";

// ── 模拟天气数据 ──────────────────────────────────────────
const weatherData = {
    "北京": { city: "北京", temperature: 22.5, condition: "晴", humidity: 45.0, windSpeed: 3.2 },
    "上海": { city: "上海", temperature: 26.0, condition: "多云", humidity: 68.0, windSpeed: 4.5 },
    "广州": { city: "广州", temperature: 30.0, condition: "雷阵雨", humidity: 82.0, windSpeed: 6.0 },
    "深圳": { city: "深圳", temperature: 29.5, condition: "晴", humidity: 70.0, windSpeed: 5.0 },
    "杭州": { city: "杭州", temperature: 25.0, condition: "阴", humidity: 65.0, windSpeed: 3.8 },
    "成都": { city: "成都", temperature: 24.0, condition: "多云", humidity: 72.0, windSpeed: 2.5 },
};

// ── 创建 MCP Server ──────────────────────────────────────
const server = new Server(
    { name: "weather-mcp-server", version: "1.0.0" },
    { capabilities: { tools: {} } }
);

// ── 注册工具列表 ──────────────────────────────────────────
server.setRequestHandler(ListToolsRequestSchema, async () => {
    return {
        tools: [
            {
                name: "get_weather",
                description: "查询指定城市的天气信息，包括温度、天气状况、湿度和风速",
                inputSchema: {
                    type: "object",
                    properties: {
                        city: {
                            type: "string",
                            description: "城市名称，如：北京、上海、广州、深圳、杭州、成都"
                        }
                    },
                    required: ["city"]
                }
            },
            {
                name: "calculate",
                description: "执行数学计算，支持加减乘除四则运算",
                inputSchema: {
                    type: "object",
                    properties: {
                        expression: {
                            type: "string",
                            description: "数学表达式，如：2 + 3 * 4 / 2"
                        }
                    },
                    required: ["expression"]
                }
            }
        ]
    };
});

// ── 处理工具调用 ──────────────────────────────────────────
server.setRequestHandler(CallToolRequestSchema, async (request) => {
    const { name, arguments: args } = request.params;

    switch (name) {
        case "get_weather": {
            const city = args.city;
            const weather = weatherData[city];
            if (weather) {
                return {
                    content: [
                        {
                            type: "text",
                            text: `城市: ${weather.city}\n温度: ${weather.temperature}°C\n天气: ${weather.condition}\n湿度: ${weather.humidity}%\n风速: ${weather.windSpeed} m/s`
                        }
                    ]
                };
            } else {
                return {
                    content: [
                        {
                            type: "text",
                            text: `未找到城市 "${city}" 的天气信息。支持的城市：北京、上海、广州、深圳、杭州、成都`
                        }
                    ],
                    isError: true
                };
            }
        }

        case "calculate": {
            const expression = args.expression;
            try {
                // 安全计算：仅允许数字和基本运算符
                const sanitized = expression.replace(/[^0-9+\-*/.()\s]/g, "");
                if (!sanitized) {
                    throw new Error("无效的表达式");
                }
                const result = Function(`"use strict"; return (${sanitized})`)();
                return {
                    content: [
                        {
                            type: "text",
                            text: `表达式: ${expression}\n结果: ${result}`
                        }
                    ]
                };
            } catch (error) {
                return {
                    content: [
                        {
                            type: "text",
                            text: `计算错误: ${error.message}`
                        }
                    ],
                    isError: true
                };
            }
        }

        default:
            return {
                content: [
                    {
                        type: "text",
                        text: `未知工具: ${name}`
                    }
                ],
                isError: true
            };
    }
});

// ── 启动服务器 (stdio 传输) ───────────────────────────────
const transport = new StdioServerTransport();
await server.connect(transport);
console.error("[Weather MCP Server] Started on stdio transport");
