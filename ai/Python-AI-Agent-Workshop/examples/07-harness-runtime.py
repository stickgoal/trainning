# -*- coding: utf-8 -*-
"""
示例 07: Agent Harness 运行时基础设施
======================================
展示 Agent Harness 核心组件：上下文管理、工具沙箱、监控、权限控制

Harness 是让 Agent 稳定运行的工程基础设施
类似 JVM 之于 Java 应用，Kubernetes 之于微服务

适合 Java 工程师阅读：Harness 各组件均有注释
"""

import time
import json
import logging
from typing import TypedDict, Callable, Any, Optional
from dataclasses import dataclass, field
from datetime import datetime
from functools import wraps


# ============================================
# 1. 日志与监控 — 可观测性基础设施
# ============================================

# Python logging 配置
# Java 类比：SLF4J + Logback 配置
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
logger = logging.getLogger("agent-harness")


@dataclass
class TraceSpan:
    """
    调用链追踪的一个 Span（一段操作的记录）。
    类似 SkyWalking / Zipkin 的 Span 概念。
    """
    span_id: str
    operation: str
    start_time: float
    end_time: Optional[float] = None
    duration_ms: Optional[float] = None
    input_data: Optional[str] = None
    output_data: Optional[str] = None
    status: str = "running"  # running / success / error

    def finish(self, output: str, status: str = "success") -> None:
        """结束 Span 记录"""
        self.end_time = time.time()
        self.duration_ms = (self.end_time - self.start_time) * 1000
        self.output_data = output[:200] if output else None
        self.status = status


class Tracer:
    """
    调用链追踪器。
    记录 Agent 执行过程中的每一步操作。
    Java 类比：类似 SkyWalking Tracer。
    """

    def __init__(self):
        self.spans: list[TraceSpan] = []
    
    def start_span(self, operation: str, input_data: str = "") -> TraceSpan:
        """开始一个新的追踪 Span"""
        span = TraceSpan(
            span_id=f"span_{len(self.spans) + 1}",
            operation=operation,
            start_time=time.time(),
            input_data=input_data[:200] if input_data else None,
        )
        self.spans.append(span)
        logger.info(f"开始追踪: {operation} (span_id={span.span_id})")
        return span
    
    def get_summary(self) -> str:
        """获取追踪摘要"""
        lines = ["=== 追踪摘要 ==="]
        for span in self.spans:
            duration = f"{span.duration_ms:.1f}ms" if span.duration_ms else "running"
            lines.append(f"  {span.span_id}: {span.operation} [{span.status}] ({duration})")
        total = sum(s.duration_ms or 0 for s in self.spans)
        lines.append(f"  总耗时: {total:.1f}ms")
        return "\n".join(lines)


# ============================================
# 2. Token 计数器 — 上下文窗口管理
# ============================================

class TokenCounter:
    """
    Token 计数器：跟踪 Token 使用量。
    
    LLM 的 Context Window 有上限（如 GPT-4o 是 128K Token）。
    Harness 需要跟踪 Token 消耗，避免超出窗口限制。
    Java 类比：类似 ByteBuffer 的 capacity/position 跟踪。
    """

    def __init__(self, max_tokens: int = 128000):
        self.max_tokens = max_tokens
        self.used_tokens = 0
        self.history: list[dict] = []
    
    def count(self, text: str) -> int:
        """
        估算文本的 Token 数。
        粗略估算：英文 1 word ≈ 1.3 token，中文 1 char ≈ 1.5 token
        """
        chinese_chars = sum(1 for c in text if '\u4e00' <= c <= '\u9fff')
        english_words = len(text.split()) - chinese_chars
        return int(chinese_chars * 1.5 + max(english_words, 0) * 1.3)
    
    def consume(self, text: str, role: str = "unknown") -> int:
        """记录 Token 消耗"""
        tokens = self.count(text)
        self.used_tokens += tokens
        self.history.append({
            "role": role,
            "tokens": tokens,
            "total": self.used_tokens,
            "timestamp": datetime.now().isoformat(),
        })
        logger.info(f"Token 消耗: {tokens} (累计: {self.used_tokens}/{self.max_tokens})")
        
        if self.used_tokens > self.max_tokens * 0.8:
            logger.warning(f"Token 使用超过 80%: {self.used_tokens}/{self.max_tokens}")
        
        return tokens
    
    def remaining(self) -> int:
        """剩余 Token 额度"""
        return self.max_tokens - self.used_tokens
    
    def needs_compression(self) -> bool:
        """是否需要压缩上下文"""
        return self.used_tokens > self.max_tokens * 0.8


# ============================================
# 3. Context Manager — 上下文管理器
# ============================================

class ContextManager:
    """
    上下文管理器：管理 LLM 的对话上下文。
    
    核心职责：
    1. 维护对话历史
    2. 当上下文接近窗口上限时自动压缩
    3. 保留关键信息，丢弃低价值内容
    
    Java 类比：类似带淘汰策略的缓存管理器。
    """

    def __init__(self, token_counter: TokenCounter):
        self.messages: list[dict] = []
        self.token_counter = token_counter
    
    def add_message(self, role: str, content: str) -> None:
        """添加消息到上下文"""
        self.messages.append({"role": role, "content": content})
        self.token_counter.consume(content, role)
        
        # 检查是否需要压缩
        if self.token_counter.needs_compression():
            self._compress()
    
    def _compress(self) -> None:
        """
        上下文压缩策略：
        1. 保留 System 消息（角色设定）
        2. 保留最近 N 轮对话
        3. 将较早的对话压缩为摘要
        
        这是 Harness 的核心能力之一：
        LLM 上下文有限 → 需要智能管理上下文
        """
        logger.info("开始上下文压缩...")
        
        # 保留 system 消息
        system_msgs = [m for m in self.messages if m["role"] == "system"]
        
        # 保留最近 4 条消息
        recent_msgs = self.messages[-4:] if len(self.messages) > 4 else self.messages
        
        # 较早的消息生成摘要（实际应用中用 LLM 生成）
        old_msgs = self.messages[len(system_msgs):-4] if len(self.messages) > 4 else []
        if old_msgs:
            summary = f"[历史对话摘要: 共 {len(old_msgs)} 条消息已压缩]"
            self.messages = system_msgs + [{"role": "system", "content": summary}] + recent_msgs
            logger.info(f"压缩完成: {len(old_msgs)} 条消息 → 1 条摘要")
    
    def get_context(self) -> list[dict]:
        """获取当前上下文（用于发送给 LLM）"""
        return self.messages.copy()
    
    def clear(self) -> None:
        """清空上下文"""
        self.messages.clear()
        logger.info("上下文已清空")


# ============================================
# 4. Tool Sandbox — 工具执行沙箱
# ============================================

@dataclass
class ToolExecution:
    """工具执行记录"""
    tool_name: str
    args: dict
    result: Any = None
    error: Optional[str] = None
    duration_ms: Optional[float] = None
    status: str = "pending"


class ToolSandbox:
    """
    工具执行沙箱：安全地执行 Agent 调用的工具。
    
    职责：
    1. 权限检查（是否有权限调用此工具）
    2. 参数校验
    3. 超时控制
    4. 错误捕获
    5. 执行日志
    
    Java 类比：类似 SecurityManager + Try-Catch + Timeout 的组合。
    """

    # 危险级别
    SAFE = "safe"          # 只读操作
    MODERATE = "moderate"  # 写操作
    DANGEROUS = "dangerous" # 危险操作（删除、系统命令等）
    
    def __init__(self):
        self.tools: dict[str, Callable] = {}          # 工具注册表
        self.permissions: dict[str, str] = {}          # 工具权限级别
        self.executions: list[ToolExecution] = []      # 执行历史
        self.tracer: Tracer = Tracer()
    
    def register(self, name: str, func: Callable, permission_level: str = "safe") -> None:
        """
        注册工具到沙箱。
        
        Callable 是 Python 类型提示，表示可调用对象
        Java 类比：Function<Args, Result> 或 Supplier<Result>
        """
        self.tools[name] = func
        self.permissions[name] = permission_level
        logger.info(f"注册工具: {name} (权限: {permission_level})")
    
    def execute(self, tool_name: str, args: dict, require_approval: bool = False) -> Any:
        """
        执行工具（带安全检查）。
        
        权限控制流程：
        1. 检查工具是否存在
        2. 检查权限级别
        3. 危险操作需要人工审批
        4. 执行并捕获异常
        5. 记录执行结果
        """
        execution = ToolExecution(tool_name=tool_name, args=args)
        
        # 检查 1: 工具是否存在
        if tool_name not in self.tools:
            execution.error = f"工具不存在: {tool_name}"
            execution.status = "error"
            self.executions.append(execution)
            raise ValueError(execution.error)
        
        # 检查 2: 权限级别
        permission = self.permissions.get(tool_name, "safe")
        if permission == self.DANGEROUS and require_approval:
            execution.error = "危险操作需要人工审批"
            execution.status = "blocked"
            self.executions.append(execution)
            logger.warning(f"工具 {tool_name} 被阻止: 需要人工审批")
            return None
        
        # 检查 3: 执行工具
        span = self.tracer.start_span(f"tool:{tool_name}", json.dumps(args, ensure_ascii=False))
        
        try:
            start = time.time()
            
            # 实际执行工具函数
            # **args 是 Python 字典解包：将 dict 的键值对作为关键字参数传入
            # 等价于 func(arg1=value1, arg2=value2, ...)
            result = self.tools[tool_name](**args)
            
            execution.duration_ms = (time.time() - start) * 1000
            execution.result = result
            execution.status = "success"
            span.finish(str(result), "success")
            
        except Exception as e:
            execution.error = str(e)
            execution.status = "error"
            execution.duration_ms = (time.time() - start) * 1000
            span.finish(str(e), "error")
            logger.error(f"工具执行失败: {tool_name} - {e}")
        
        self.executions.append(execution)
        return execution.result
    
    def get_execution_log(self) -> str:
        """获取执行日志"""
        lines = ["=== 工具执行日志 ==="]
        for exec_record in self.executions:
            duration = f"{exec_record.duration_ms:.1f}ms" if exec_record.duration_ms else "-"
            status = exec_record.status
            error = f" 错误: {exec_record.error}" if exec_record.error else ""
            lines.append(f"  {exec_record.tool_name}({exec_record.args}) [{status}] ({duration}){error}")
        return "\n".join(lines)


# ============================================
# 5. Feedback Collector — 反馈收集器
# ============================================

@dataclass
class Feedback:
    """用户反馈"""
    message_id: str
    rating: int  # 1-5 分
    comment: str = ""
    timestamp: str = field(default_factory=lambda: datetime.now().isoformat())


class FeedbackCollector:
    """
    反馈收集器：收集用户对 Agent 回答的反馈。
    用于持续改进 Agent 质量。
    Java 类比：类似评价系统的 Repository。
    """
    
    def __init__(self):
        self.feedbacks: list[Feedback] = []
    
    def collect(self, message_id: str, rating: int, comment: str = "") -> None:
        """收集用户反馈"""
        feedback = Feedback(message_id=message_id, rating=rating, comment=comment)
        self.feedbacks.append(feedback)
        logger.info(f"收到反馈: message={message_id}, 评分={rating}/5")
    
    def get_average_rating(self) -> float:
        """获取平均评分"""
        if not self.feedbacks:
            return 0.0
        return sum(f.rating for f in self.feedbacks) / len(self.feedbacks)
    
    def get_summary(self) -> str:
        """获取反馈摘要"""
        return f"反馈总数: {len(self.feedbacks)}, 平均评分: {self.get_average_rating():.1f}/5"


# ============================================
# 6. Agent Harness — 完整运行时
# ============================================

class AgentHarness:
    """
    Agent Harness：将所有基础设施组件组合在一起。
    
    这是 Agent 稳定运行的工程保障。
    没有 Harness，Agent 就只是一个 LLM 调用脚本。
    有了 Harness，Agent 才是生产可用的系统。
    
    Java 类比：
    没有 Harness 的 Agent = 一个 main 方法直接调 API 的 Java 程序
    有 Harness 的 Agent = 运行在 Spring + JVM + 监控体系中的 Java 应用
    """
    
    def __init__(self, max_tokens: int = 128000):
        self.tracer = Tracer()
        self.token_counter = TokenCounter(max_tokens=max_tokens)
        self.context = ContextManager(self.token_counter)
        self.sandbox = ToolSandbox()
        self.feedback = FeedbackCollector()
    
    def setup_system_prompt(self, prompt: str) -> None:
        """设置系统 Prompt"""
        self.context.add_message("system", prompt)
    
    def register_tool(self, name: str, func: Callable, permission: str = "safe") -> None:
        """注册工具"""
        self.sandbox.register(name, func, permission)
    
    def call_tool(self, tool_name: str, args: dict) -> Any:
        """Agent 调用工具（通过 Harness 安全执行）"""
        return self.sandbox.execute(tool_name, args)
    
    def add_user_message(self, message: str) -> None:
        """添加用户消息"""
        self.context.add_message("user", message)
    
    def add_assistant_message(self, message: str) -> None:
        """添加 AI 回复"""
        self.context.add_message("assistant", message)
    
    def get_status(self) -> str:
        """获取 Harness 状态报告"""
        return f"""
=== Agent Harness 状态 ===
Token 使用: {self.token_counter.used_tokens}/{self.token_counter.max_tokens}
上下文消息数: {len(self.context.messages)}
注册工具数: {len(self.sandbox.tools)}
工具执行次数: {len(self.sandbox.executions)}
追踪 Span 数: {len(self.tracer.spans)}
{self.feedback.get_summary()}
"""


# ============================================
# 7. 运行 Harness 示例
# ============================================

def main():
    print("=" * 60)
    print("Agent Harness 运行时示例")
    print("=" * 60)
    
    # 创建 Harness
    harness = AgentHarness(max_tokens=4000)  # 设置较小的窗口以演示压缩
    
    # 注册工具
    def search_docs(query: str) -> str:
        """文档搜索工具"""
        return f"搜索 '{query}' 的结果：找到3个相关文档"
    
    def execute_code(code: str) -> str:
        """代码执行工具"""
        return f"执行代码完成，输出: OK"
    
    def delete_file(path: str) -> str:
        """删除文件工具（危险操作）"""
        return f"已删除文件: {path}"
    
    harness.register_tool("search_docs", search_docs, "safe")
    harness.register_tool("execute_code", execute_code, "moderate")
    harness.register_tool("delete_file", delete_file, "dangerous")
    
    # 设置系统 Prompt
    harness.setup_system_prompt("你是一个企业知识库助手，可以搜索文档和执行代码。")
    
    # 模拟对话
    print("\n--- 模拟对话 ---")
    harness.add_user_message("帮我搜索关于 Python 异步编程的文档")
    harness.call_tool("search_docs", {"query": "Python 异步编程"})
    harness.add_assistant_message("找到了3个关于 Python 异步编程的文档...")
    
    harness.add_user_message("帮我执行一段测试代码")
    harness.call_tool("execute_code", {"code": "print('Hello')"})
    harness.add_assistant_message("代码执行成功，输出: Hello")
    
    # 危险操作被阻止
    print("\n--- 危险操作拦截 ---")
    harness.call_tool("delete_file", {"path": "/etc/passwd"}, require_approval=True)
    
    # 收集反馈
    harness.feedback.collect("msg_001", 5, "回答很准确")
    harness.feedback.collect("msg_002", 4, "响应稍慢")
    
    # 输出状态
    print(harness.get_status())
    
    print("\n" + harness.tracer.get_summary())
    print("\n" + harness.sandbox.get_execution_log())
    
    # 演示上下文压缩
    print("\n--- 上下文压缩演示 ---")
    for i in range(20):
        harness.add_user_message(f"这是第 {i+1} 条测试消息，用于触发上下文压缩...")
    
    print(f"压缩后上下文消息数: {len(harness.context.messages)}")
    print("(较早的消息已被压缩为摘要)")
    
    print("\n=== Harness 架构总结 ===")
    print("""
    ┌─────────────────────────────────────────┐
    │            Agent Harness                │
    │                                         │
    │  ┌─────────────┐  ┌──────────────────┐ │
    │  │  Context    │  │  Token Counter   │ │
    │  │  Manager    │──│  (窗口管理)       │ │
    │  │  (上下文)    │  │                  │ │
    │  └─────────────┘  └──────────────────┘ │
    │                                         │
    │  ┌─────────────┐  ┌──────────────────┐ │
    │  │  Tool       │  │  Permission      │ │
    │  │  Sandbox    │──│  Check           │ │
    │  │  (工具沙箱)  │  │  (权限控制)       │ │
    │  └─────────────┘  └──────────────────┘ │
    │                                         │
    │  ┌─────────────┐  ┌──────────────────┐ │
    │  │  Tracer     │  │  Feedback        │ │
    │  │  (追踪)     │  │  Collector       │ │
    │  │             │  │  (反馈收集)       │ │
    │  └─────────────┘  └──────────────────┘ │
    └─────────────────────────────────────────┘
    """)


if __name__ == "__main__":
    main()
