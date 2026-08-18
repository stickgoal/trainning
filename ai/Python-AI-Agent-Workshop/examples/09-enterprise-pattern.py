# -*- coding: utf-8 -*-
"""
示例 09: 企业级 Agent 架构模式
================================
展示企业级 Agent 应用的架构模式和最佳实践

整合所有组件：LLM + Memory + RAG + Tools + MCP + A2A + Harness + Skill + Observability

适合 Java 工程师阅读：企业架构模式与 Spring 生态类比
"""

import asyncio
import logging
from typing import TypedDict, Optional, Callable
from dataclasses import dataclass, field
from datetime import datetime

# 注意：以下是架构模式展示，部分代码为伪代码/示意代码
# 实际运行需要安装相应依赖并配置 API Key

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("enterprise-agent")


# ============================================
# 1. 企业级 Agent 配置
# ============================================

@dataclass
class AgentConfig:
    """
    Agent 全局配置。
    集中管理所有组件的配置参数。
    Java 类比：类似 Spring 的 application.yml 配置类。
    """
    # LLM 配置
    llm_model: str = "gpt-4o"
    llm_temperature: float = 0.3
    llm_max_tokens: int = 2000
    
    # 上下文管理配置
    context_window: int = 128000
    context_compression_threshold: float = 0.8  # 80% 时触发压缩
    
    # 工具配置
    tool_timeout_seconds: int = 30
    max_tool_iterations: int = 10
    
    # 安全配置
    require_approval_for_dangerous: bool = True
    audit_all_tool_calls: bool = True
    
    # 监控配置
    enable_tracing: bool = True
    enable_metrics: bool = True
    
    # 成本控制
    max_daily_token_budget: int = 1_000_000  # 每日 Token 预算


# ============================================
# 2. 企业级 Agent 架构分层
# ============================================

class EnterpriseAgentArchitecture:
    """
    企业级 Agent 架构模式展示。
    
    架构分层（自上而下）：
    1. 接入层 — API Gateway / Web UI / CLI
    2. 应用层 — Agent Application / Session Manager / Skill Router
    3. 核心服务层 — LLM Service / Memory / Knowledge / Tools
    4. 协议层 — MCP Client / A2A Hub
    5. 工程层 — Harness / Evaluation / Observability / Security
    6. 企业系统层 — DB / ERP / Git / CI/CD / Wiki
    
    Java 类比：
    类似 Spring Boot 应用的分层架构：
    Controller → Service → Repository → Database
    但这里每层都有 AI 特有的组件。
    """
    
    def __init__(self, config: AgentConfig):
        self.config = config
        self._setup_layers()
    
    def _setup_layers(self):
        """初始化各层组件"""
        logger.info("初始化企业级 Agent 架构...")
        
        # 接入层
        self.api_gateway = APIGateway()
        
        # 应用层
        self.session_manager = SessionManager()
        self.skill_router = SkillRouter()
        self.workflow_engine = WorkflowEngine()
        
        # 核心服务层
        self.llm_service = LLMService(self.config)
        self.memory_service = MemoryService()
        self.knowledge_service = KnowledgeService()
        self.tool_hub = ToolHub()
        
        # 协议层
        self.mcp_client = MCPClientManager()
        self.a2a_hub = A2AHub()
        
        # 工程层
        self.harness = AgentHarness(self.config)
        self.evaluator = QualityEvaluator()
        self.observability = ObservabilityStack()
        self.security = SecurityManager(self.config)
        
        logger.info("企业级 Agent 架构初始化完成")


# ============================================
# 3. 接入层 — API Gateway
# ============================================

class APIGateway:
    """
    API 网关：统一入口，处理认证、限流、路由。
    Java 类比：Spring Cloud Gateway / Nginx。
    """
    
    def __init__(self):
        self.routes: dict[str, Callable] = {}
        self.rate_limiter = RateLimiter()
    
    def handle_request(self, request: dict) -> dict:
        """处理进入的请求"""
        logger.info(f"API Gateway 收到请求: {request.get('action', 'unknown')}")
        
        # 1. 认证检查
        # 2. 限流检查
        # 3. 路由到对应处理逻辑
        # 4. 返回响应
        
        return {"status": "processed", "request_id": request.get("id")}


class RateLimiter:
    """速率限制器"""
    def __init__(self, max_requests_per_minute: int = 60):
        self.max_rpm = max_requests_per_minute


# ============================================
# 4. 应用层 — 会话管理 / 技能路由 / 工作流引擎
# ============================================

class SessionManager:
    """
    会话管理器：管理用户会话状态。
    Java 类比：Spring Session / HttpSession 管理。
    """
    
    def __init__(self):
        self.sessions: dict[str, dict] = {}
    
    def create_session(self, user_id: str) -> str:
        """创建新会话"""
        session_id = f"session_{len(self.sessions) + 1}"
        self.sessions[session_id] = {
            "user_id": user_id,
            "created_at": datetime.now().isoformat(),
            "message_count": 0,
        }
        logger.info(f"创建会话: {session_id} (用户: {user_id})")
        return session_id


class SkillRouter:
    """
    技能路由器：根据用户请求匹配技能。
    实际应用中用 LLM + Embedding 做智能匹配。
    """
    
    def __init__(self):
        self.skills: dict[str, dict] = {}
    
    def register_skill(self, name: str, description: str, handler: Callable) -> None:
        """注册技能"""
        self.skills[name] = {"description": description, "handler": handler}


class WorkflowEngine:
    """
    工作流引擎：基于 LangGraph 编排复杂任务。
    Java 类比：Spring StateMachine / Camunda BPM。
    """
    
    def __init__(self):
        self.workflows: dict[str, dict] = {}


# ============================================
# 5. 核心服务层 — LLM / Memory / Knowledge / Tools
# ============================================

class LLMService:
    """LLM 服务：封装模型调用，支持多模型路由"""
    
    def __init__(self, config: AgentConfig):
        self.config = config
        self.models: dict[str, dict] = {
            "gpt-4o": {"provider": "openai", "max_tokens": 128000},
            "claude-3.5": {"provider": "anthropic", "max_tokens": 200000},
            "glm-4": {"provider": "zhipu", "max_tokens": 128000},
        }
    
    async def invoke(self, messages: list[dict], model: str = None) -> str:
        """调用 LLM（伪代码）"""
        model = model or self.config.llm_model
        logger.info(f"LLM 调用: model={model}, messages={len(messages)}")
        # 实际实现中这里会调用对应的 LLM API
        return "LLM 响应内容"


class MemoryService:
    """
    记忆服务：管理短期和长期记忆。
    Java 类比：Redis (短期) + PostgreSQL (长期)。
    """
    
    def __init__(self):
        self.short_term: dict[str, list] = {}  # 短期记忆（会话级）
        self.long_term: dict[str, dict] = {}   # 长期记忆（持久化）
    
    def add_to_short_term(self, session_id: str, message: dict) -> None:
        """添加到短期记忆"""
        if session_id not in self.short_term:
            self.short_term[session_id] = []
        self.short_term[session_id].append(message)
    
    def add_to_long_term(self, user_id: str, key: str, value: str) -> None:
        """添加到长期记忆"""
        if user_id not in self.long_term:
            self.long_term[user_id] = {}
        self.long_term[user_id][key] = {
            "value": value,
            "timestamp": datetime.now().isoformat(),
        }


class KnowledgeService:
    """
    知识服务：RAG 检索增强。
    包含向量数据库和检索器。
    Java 类比：Elasticsearch Service + 索引管理。
    """
    
    def __init__(self):
        self.vector_store: dict = {}  # 向量数据库连接
        self.retriever: dict = {}     # 检索器配置
    
    async def retrieve(self, query: str, top_k: int = 5) -> list[dict]:
        """检索相关知识"""
        logger.info(f"知识检索: query='{query}', top_k={top_k}")
        # 实际实现中这里会调用向量数据库
        return [{"content": "检索到的文档内容", "score": 0.95}]


class ToolHub:
    """
    工具中心：管理所有可用工具。
    Java 类比：Service Registry / Plugin Manager。
    """
    
    def __init__(self):
        self.tools: dict[str, dict] = {}
    
    def register(self, name: str, handler: Callable, permission: str = "safe") -> None:
        """注册工具"""
        self.tools[name] = {"handler": handler, "permission": permission}
    
    async def execute(self, name: str, args: dict) -> str:
        """执行工具"""
        if name not in self.tools:
            raise ValueError(f"工具未注册: {name}")
        return f"工具 {name} 执行结果"


# ============================================
# 6. 协议层 — MCP / A2A
# ============================================

class MCPClientManager:
    """
    MCP 客户端管理器：管理与所有 MCP Server 的连接。
    Java 类比：连接池管理多个 JDBC 连接。
    """
    
    def __init__(self):
        self.servers: dict[str, dict] = {}
    
    def connect(self, server_name: str, server_url: str) -> None:
        """连接到 MCP Server"""
        self.servers[server_name] = {"url": server_url, "status": "connected"}
        logger.info(f"MCP Server 连接: {server_name} @ {server_url}")
    
    async def list_tools(self, server_name: str) -> list[dict]:
        """获取 MCP Server 提供的工具列表"""
        return [{"name": "tool_1", "description": "示例工具"}]
    
    async def call_tool(self, server_name: str, tool_name: str, args: dict) -> str:
        """通过 MCP 调用工具"""
        logger.info(f"MCP 工具调用: server={server_name}, tool={tool_name}")
        return "MCP 工具执行结果"


class A2AHub:
    """
    A2A 通信中心：管理 Agent 间通信。
    Java 类比：消息总线 / 事件总线（EventBus）。
    """
    
    def __init__(self):
        self.agents: dict[str, dict] = {}
        self.message_queue: list[dict] = []
    
    def register_agent(self, agent_id: str, capabilities: list[str]) -> None:
        """注册 Agent 及其能力"""
        self.agents[agent_id] = {"capabilities": capabilities, "status": "online"}
    
    async def send_message(self, from_id: str, to_id: str, message: dict) -> None:
        """Agent 间发送消息"""
        self.message_queue.append({
            "from": from_id,
            "to": to_id,
            "message": message,
            "timestamp": datetime.now().isoformat(),
        })


# ============================================
# 7. 工程层 — Harness / Evaluation / Observability / Security
# ============================================

class AgentHarness:
    """
    Agent 运行时基础设施。
    整合上下文管理、工具沙箱、监控等。
    """
    
    def __init__(self, config: AgentConfig):
        self.config = config
        self.token_used_today = 0
    
    def check_budget(self) -> bool:
        """检查 Token 预算"""
        return self.token_used_today < self.config.max_daily_token_budget


class QualityEvaluator:
    """
    质量评估器：评估 Agent 输出质量。
    Java 类比：测试框架 + 代码质量工具（SonarQube）。
    """
    
    def __init__(self):
        self.evaluations: list[dict] = []
    
    async def evaluate(self, response: str, criteria: dict) -> dict:
        """评估 Agent 回答质量"""
        return {
            "relevance": 0.9,      # 相关性
            "accuracy": 0.85,       # 准确性
            "completeness": 0.8,    # 完整性
            "safety": 1.0,          # 安全性
        }


class ObservabilityStack:
    """
    可观测性栈：日志 + 指标 + 追踪。
    Java 类比：ELK + Prometheus + SkyWalking。
    """
    
    def __init__(self):
        self.traces: list[dict] = []
        self.metrics: dict[str, float] = {}
        self.logs: list[dict] = []
    
    def record_metric(self, name: str, value: float) -> None:
        """记录指标"""
        self.metrics[name] = value
    
    def record_trace(self, trace: dict) -> None:
        """记录追踪"""
        self.traces.append(trace)


class SecurityManager:
    """
    安全管理器：权限控制、输入过滤、输出审查。
    Java 类比：Spring Security。
    """
    
    def __init__(self, config: AgentConfig):
        self.config = config
        self.audit_log: list[dict] = []
    
    def check_input(self, user_input: str) -> bool:
        """检查输入安全性（防 Prompt Injection）"""
        dangerous_patterns = ["ignore previous", "system:", "forget your instructions"]
        return not any(p in user_input.lower() for p in dangerous_patterns)
    
    def check_output(self, agent_output: str) -> str:
        """检查输出安全性（过滤敏感信息）"""
        # 实际应用中会做更复杂的敏感信息检测
        return agent_output
    
    def audit(self, action: str, details: dict) -> None:
        """记录审计日志"""
        self.audit_log.append({
            "action": action,
            "details": details,
            "timestamp": datetime.now().isoformat(),
        })


# ============================================
# 8. 企业级 Agent 执行流程
# ============================================

async def enterprise_agent_flow():
    """
    展示企业级 Agent 的完整执行流程。
    
    从用户请求到最终回答，经过所有架构层的处理。
    """
    print("=" * 60)
    print("企业级 Agent 架构执行流程")
    print("=" * 60)
    
    config = AgentConfig()
    arch = EnterpriseAgentArchitecture(config)
    
    # 模拟用户请求
    user_request = "帮我查询公司差旅报销政策，并生成一份报销申请模板"
    user_id = "user_001"
    
    print(f"\n用户请求: {user_request}")
    print(f"用户ID: {user_id}\n")
    
    # --- 接入层 ---
    print("【接入层】API Gateway 处理请求...")
    arch.api_gateway.handle_request({"action": "chat", "content": user_request})
    
    # --- 安全层 ---
    print("\n【安全层】输入安全检查...")
    is_safe = arch.security.check_input(user_request)
    print(f"  安全检查: {'通过' if is_safe else '拦截'}")
    arch.security.audit("user_request", {"user_id": user_id, "input": user_request})
    
    if not is_safe:
        print("  请求被安全系统拦截！")
        return
    
    # --- 应用层 ---
    print("\n【应用层】会话管理 + 技能路由...")
    session_id = arch.session_manager.create_session(user_id)
    print(f"  会话ID: {session_id}")
    
    # --- 核心服务层：知识检索 (RAG) ---
    print("\n【核心服务层】知识检索 (RAG)...")
    knowledge = await arch.knowledge_service.retrieve("差旅报销政策", top_k=3)
    print(f"  检索到 {len(knowledge)} 条相关知识")
    
    # --- 核心服务层：LLM 调用 ---
    print("\n【核心服务层】LLM 推理生成...")
    messages = [
        {"role": "system", "content": "你是一个企业知识助手。"},
        {"role": "user", "content": user_request},
    ]
    response = await arch.llm_service.invoke(messages)
    print(f"  LLM 响应长度: {len(response)} 字符")
    
    # --- 协议层：MCP 工具调用 ---
    print("\n【协议层】MCP 工具调用...")
    arch.mcp_client.connect("hr-system", "mcp://hr-server:8080")
    hr_info = await arch.mcp_client.call_tool("hr-system", "get_policy", {"type": "travel"})
    print(f"  HR 系统返回: {hr_info[:50]}...")
    
    # --- 工程层：质量评估 ---
    print("\n【工程层】质量评估...")
    evaluation = await arch.evaluator.evaluate(response, {"criteria": "accuracy"})
    print(f"  评估结果: {evaluation}")
    
    # --- 工程层：可观测性 ---
    print("\n【工程层】可观测性记录...")
    arch.observability.record_metric("response_time_ms", 1250)
    arch.observability.record_metric("token_used", 1500)
    arch.observability.record_metric("tools_called", 1)
    print(f"  指标记录完成: {arch.observability.metrics}")
    
    # --- 安全层：输出审查 ---
    print("\n【安全层】输出安全审查...")
    safe_output = arch.security.check_output(response)
    print(f"  输出审查: 通过")
    
    # --- 最终响应 ---
    print("\n" + "=" * 60)
    print("最终响应:")
    print("=" * 60)
    print(safe_output[:200])
    
    # --- 架构图输出 ---
    print("\n" + "=" * 60)
    print("企业级 Agent 架构全景")
    print("=" * 60)
    print("""
    用户请求
        │
    ┌───▼──────────────────────────────────────────────┐
    │                   接入层                           │
    │   API Gateway (认证 / 限流 / 路由)                 │
    └───┬──────────────────────────────────────────────┘
        │
    ┌───▼──────────────────────────────────────────────┐
    │                   应用层                           │
    │   Session Manager │ Skill Router │ Workflow Engine│
    └───┬──────────────────────────────────────────────┘
        │
    ┌───▼──────────────────────────────────────────────┐
    │                 核心服务层                         │
    │   LLM Service  │ Memory │ Knowledge(RAG) │ Tools  │
    └───┬──────────────────────────────────────────────┘
        │
    ┌───▼──────────────────────────────────────────────┐
    │                  协议层                            │
    │   MCP Client (工具标准化)  │  A2A Hub (Agent通信)  │
    └───┬──────────────────────────────────────────────┘
        │
    ┌───▼──────────────────────────────────────────────┐
    │                  工程层                            │
    │   Harness │ Evaluation │ Observability │ Security │
    └───┬──────────────────────────────────────────────┘
        │
    ┌───▼──────────────────────────────────────────────┐
    │                企业系统层                          │
    │   DB │ ERP │ Git │ CI/CD │ Wiki │ Slack/飞书      │
    └──────────────────────────────────────────────────┘
    """)


# ============================================
# 9. 运行
# ============================================

if __name__ == "__main__":
    asyncio.run(enterprise_agent_flow())
