# -*- coding: utf-8 -*-
"""
示例 05: MCP Server 创建与 Tool 暴露
======================================
展示如何创建 MCP Server 并将工具暴露给 LLM Agent

MCP (Model Context Protocol) 是工具调用的标准化协议
类似 JDBC 统一了数据库访问，MCP 统一了 LLM 工具调用

适合 Java 工程师阅读：MCP 架构和 Tool 暴露机制均有注释
"""

from mcp import Server, Tool
from mcp.types import TextContent
import json
import asyncio


# ============================================
# 1. MCP Server 创建
# ============================================

# MCP Server 类似一个微服务，向 LLM 暴露工具能力
# Java 类比：类似 Spring Boot 的 REST Controller，但接口是标准化的

server = Server("enterprise-tools")


# ============================================
# 2. Tool 定义 — 标准化工具接口
# ============================================

# MCP Tool 定义与 LangChain Tool 类似，但使用 MCP 标准格式
# 关键区别：MCP Tool 是跨应用复用的（任何支持 MCP 的 LLM 都能用）

# 定义工具的 Schema（告诉 LLM 这个工具能做什么、需要什么参数）
# 这与 OpenAI Tool Calling 的 Schema 格式一致

search_tool = Tool(
    name="search_employee",
    description="搜索企业员工信息，按姓名或工号查询",
    inputSchema={
        "type": "object",
        "properties": {
            "query": {
                "type": "string",
                "description": "员工姓名或工号，如 '张三' 或 'EMP001'",
            },
            "department": {
                "type": "string",
                "description": "部门名称（可选筛选条件）",
            },
        },
        "required": ["query"],
    },
)

database_tool = Tool(
    name="query_database",
    description="执行 SQL 查询并返回结果（只读查询）",
    inputSchema={
        "type": "object",
        "properties": {
            "sql": {
                "type": "string",
                "description": "SQL SELECT 查询语句",
            },
            "limit": {
                "type": "integer",
                "description": "返回行数上限，默认100",
                "default": 100,
            },
        },
        "required": ["sql"],
    },
)

file_tool = Tool(
    name="read_file",
    description="读取服务器上的文件内容",
    inputSchema={
        "type": "object",
        "properties": {
            "path": {
                "type": "string",
                "description": "文件路径（绝对路径）",
            },
        },
        "required": ["path"],
    },
)


# ============================================
# 3. Tool Handler 实现 — 工具的实际执行逻辑
# ============================================

# @server.list_tools() 是 Python 装饰器
# 注册下面的函数为 MCP Server 的工具列表处理器
# 当 MCP Client 请求工具列表时，调用这个函数
# Java 类比：类似 @GetMapping 注解的路由映射

@server.list_tools()
async def list_tools() -> list[Tool]:
    """
    返回 MCP Server 提供的所有工具列表。
    
    async 关键字：表示这是异步函数
    在 Python 中，async/await 类似 Java 的 CompletableFuture
    async def → 返回协程对象
    await → 等待异步操作完成
    
    Java 类比：
    public CompletableFuture<List<Tool>> listTools() {
        return CompletableFuture.completedFuture(List.of(searchTool, ...));
    }
    """
    return [search_tool, database_tool, file_tool]


@server.call_tool()
async def call_tool(name: str, arguments: dict) -> list[TextContent]:
    """
    工具调用处理器：根据工具名执行对应操作并返回结果。
    
    这是 MCP 的核心：LLM → MCP Client → call_tool() → 实际执行
    
    Args:
        name: 工具名称（如 "search_employee"）
        arguments: 工具参数（如 {"query": "张三"}）
    
    Returns:
        TextContent 列表：工具执行结果
    """
    # 根据工具名分发到不同的处理逻辑
    # 类似 Java 的 switch-case 或策略模式

    if name == "search_employee":
        result = await handle_search_employee(arguments)
    elif name == "query_database":
        result = await handle_query_database(arguments)
    elif name == "read_file":
        result = await handle_read_file(arguments)
    else:
        result = f"未知工具: {name}"

    # TextContent：MCP 标准的文本内容返回格式
    return [TextContent(type="text", text=result)]


async def handle_search_employee(args: dict) -> str:
    """处理员工搜索"""
    query = args.get("query", "")
    department = args.get("department", "")

    # 模拟数据库查询（实际应用中连接 HR 系统）
    employees = [
        {"id": "EMP001", "name": "张三", "department": "技术部", "position": "高级工程师"},
        {"id": "EMP002", "name": "李四", "department": "产品部", "position": "产品经理"},
        {"id": "EMP003", "name": "王五", "department": "技术部", "position": "架构师"},
    ]

    # Python 列表推导式 + 条件过滤
    # 类似 Java Stream: employees.stream().filter(e -> e.contains(query)).collect()
    results = [
        emp for emp in employees
        if query in emp["name"] or query in emp["id"]
    ]
    if department:
        results = [emp for emp in results if department in emp["department"]]

    if results:
        return json.dumps(results, ensure_ascii=False, indent=2)
    return f"未找到匹配 '{query}' 的员工"


async def handle_query_database(args: dict) -> str:
    """处理数据库查询"""
    sql = args.get("sql", "")
    limit = args.get("limit", 100)

    # 安全检查：只允许 SELECT 查询
    # 这是 MCP Server 的安全职责之一
    if not sql.strip().upper().startswith("SELECT"):
        return "错误：只允许 SELECT 查询"

    # 模拟查询结果
    return json.dumps({
        "sql": sql,
        "row_count": 2,
        "rows": [
            {"id": 1, "name": "项目A", "status": "进行中"},
            {"id": 2, "name": "项目B", "status": "已完成"},
        ],
    }, ensure_ascii=False, indent=2)


async def handle_read_file(args: dict) -> str:
    """处理文件读取"""
    path = args.get("path", "")

    try:
        # Python Context Manager（上下文管理器）
        # with 语句：自动管理资源的打开和关闭
        # 类似 Java 的 try-with-resources
        with open(path, "r", encoding="utf-8") as f:
            content = f.read(4096)  # 限制读取长度
        return content
    except FileNotFoundError:
        return f"错误：文件不存在: {path}"
    except PermissionError:
        return f"错误：无权限访问: {path}"


# ============================================
# 4. MCP Server 启动
# ============================================

async def main():
    """
    MCP Server 主函数。
    
    MCP Server 通过 stdio 或 HTTP 与 MCP Client 通信。
    MCP Client 通常内嵌在 LLM 应用中。
    
    通信流程：
    1. LLM 应用启动 → MCP Client 连接 MCP Server
    2. MCP Client 调用 list_tools() 获取工具列表
    3. LLM 决定调用工具 → MCP Client 发送 call_tool 请求
    4. MCP Server 执行工具 → 返回结果
    5. LLM 根据工具结果生成最终回答
    """
    print("=== MCP Server 启动 ===")
    print(f"Server 名称: {server.name}")
    print(f"可用工具:")
    print(f"  - search_employee: 搜索企业员工信息")
    print(f"  - query_database: 执行 SQL 查询（只读）")
    print(f"  - read_file: 读取文件内容")
    print()

    # 模拟工具调用流程
    print("=== 模拟工具调用流程 ===")

    # 1. 模拟 LLM 决定调用 search_employee 工具
    print("\n1. LLM 决定调用工具: search_employee")
    print("   参数: {'query': '张三', 'department': '技术部'}")

    result = await call_tool("search_employee", {"query": "张三", "department": "技术部"})
    print(f"   结果: {result[0].text}")

    # 2. 模拟 LLM 决定调用 query_database 工具
    print("\n2. LLM 决定调用工具: query_database")
    print("   参数: {'sql': 'SELECT * FROM projects', 'limit': 5}")

    result = await call_tool("query_database", {"sql": "SELECT * FROM projects", "limit": 5})
    print(f"   结果: {result[0].text}")

    print("\n=== MCP 协议架构 ===")
    print("""
    ┌──────────────┐
    │   LLM 应用   │
    │  (Agent)     │
    └──────┬───────┘
           │ MCP Protocol
           ▼
    ┌──────────────┐     ┌──────────────────┐
    │  MCP Client  │────▶│   MCP Server      │
    │              │     │                   │
    │ list_tools() │     │ search_employee   │
    │ call_tool()  │     │ query_database    │
    │              │     │ read_file         │
    └──────────────┘     └──────────────────┘
                                │
                                ▼
                    ┌───────────────────┐
                    │   外部资源         │
                    │ HR系统 / DB / 文件 │
                    └───────────────────┘
    """)

    print("""
    MCP vs 传统工具集成对比:

    传统方式：
    LLM App A → 自定义集成 → HR API
    LLM App B → 自定义集成 → HR API  （重复工作）
    LLM App C → 自定义集成 → HR API  （重复工作）

    MCP 方式：
    LLM App A → MCP Client ─┐
    LLM App B → MCP Client ─┼→ MCP Server → HR API  （一次实现）
    LLM App C → MCP Client ─┘
    """)


# Python 异步入口点
# asyncio.run() 启动异步事件循环
# 类似 Java 的 main 方法 + ExecutorService
if __name__ == "__main__":
    asyncio.run(main())
