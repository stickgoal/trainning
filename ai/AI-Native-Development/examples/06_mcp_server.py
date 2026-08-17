"""
============================================================
 06_mcp_server.py — MCP Server + Client 完整示例
 对应章节：第7章 MCP（Model Context Protocol）
============================================================

演示：
 - 用 FastMCP 创建一个 MCP Server，暴露两个工具
 - 用 MCP Client 发现并调用工具
 - 说明 Tool 如何暴露给 Agent

【运行流程】
 pip install mcp
 # 方式A：作为 Server 运行（被其他 Client 调）
 python examples/06_mcp_server.py --server
 # 方式B：作为 Client 调本地 Server
 python examples/06_mcp_server.py --client

【应用场景】
 让一个 Agent 接入公司内部 N 个系统（DB / API / 文件），
 不必每个都手写适配 —— 用 MCP 统一协议。

【Python 特殊语法提示】
 - @mcp.tool() decorator : 装饰器，把普通函数转成 MCP Tool（Java 注解 @RpcMethod）
 - async def / await     : 异步函数，await 等待异步操作完成
 - if __name__ == ...    : 当作脚本入口
 - sys.argv              : 命令行参数列表
"""

import sys
import os


# =================================================================
# MCP Server 侧：暴露工具
# =================================================================
def build_server():
    """
    用 FastMCP 创建 Server。
    FastMCP 是 MCP 官方提供的 Python 高层封装，几行代码就能起一个 Server。
    """
    from mcp.server.fastmcp import FastMCP

    mcp = FastMCP("enterprise-db-tools")

    # ★ @mcp.tool() 把下面的普通函数注册成 MCP Tool
    # 函数的 docstring 会自动作为 Tool 的 description 发给 LLM
    # 函数的类型提示会自动转成 JSON Schema（参数说明）
    @mcp.tool()
    def query_employee(emp_id: str) -> dict:
        """
        根据员工 ID 查询员工信息。
        参数 emp_id: 员工工号，例如 "E001"
        返回：包含 name / dept / title 的字典
        """
        # 真实场景这里连数据库；此处用模拟数据
        db = {
            "E001": {"name": "张三", "dept": "研发", "title": "高级工程师"},
            "E002": {"name": "李四", "dept": "产品", "title": "产品经理"},
        }
        return db.get(emp_id, {"error": f"员工 {emp_id} 不存在"})

    @mcp.tool()
    def list_pending_approvals(dept: str) -> list:
        """
        查询某部门待审批工单。
        参数 dept: 部门名称
        返回：工单列表
        """
        # 模拟数据
        return [
            {"id": "T1001", "title": f"{dept} 服务器扩容", "amount": 50000},
            {"id": "T1002", "title": f"{dept} 外包采购", "amount": 120000},
        ]

    return mcp


# =================================================================
# MCP Client 侧：发现 + 调用工具
# =================================================================
async def run_client():
    """
    演示 Client 如何：
    1. 连接 MCP Server
    2. list_tools 发现可用工具
    3. 调用具体工具
    """
    from mcp import ClientSession, StdioServerParameters
    from mcp.client.stdio import stdio_client

    # Client 通过 stdio 启动并连接 Server 子进程
    # 真实场景：Server 可以是远程的（SSE / WebSocket）
    server_params = StdioServerParameters(
        command=sys.executable,         # 用当前 Python 解释器
        args=[__file__, "--server"],    # 启动本文件作为 server
    )

    # async with：Context Manager 的异步版，自动管理连接生命周期
    # 类比 Java 的 try-with-resources
    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()  # 握手

            # 1. 发现工具
            tools = await session.list_tools()
            print(f"[Client] 发现 {len(tools.tools)} 个工具：")
            for t in tools.tools:
                print(f"  - {t.name}: {t.description[:40]}")

            # 2. 调用工具：查员工
            r1 = await session.call_tool("query_employee", {"emp_id": "E001"})
            print(f"\n[Client] query_employee('E001') →")
            print(f"  {r1.content[0].text}")

            # 3. 调用工具：查待审批
            r2 = await session.call_tool("list_pending_approvals", {"dept": "研发"})
            print(f"\n[Client] list_pending_approvals('研发') →")
            print(f"  {r2.content[0].text}")


# =================================================================
# Agent 如何用 MCP 工具：把 MCP Tool 暴露给 LLM
# =================================================================
def demo_agent_with_mcp():
    """
    说明性伪代码：Agent 通过 MCP 接入工具的典型模式。

    伪代码（不实际运行）：

        # 1. 用 LangChain 的 MultiServerMCPClient 连多个 MCP Server
        from langchain_mcp_adapters.client import MultiServerMCPClient
        client = MultiServerMCPClient({
            "db": {"command": "python", "args": ["db_server.py"]},
            "api": {"url": "http://api.mcp.example.com/sse"},
        })

        # 2. 异步加载所有工具
        tools = await client.get_tools()
        # → tools 现在是 [query_employee, list_pending_approvals, ...]

        # 3. 把 tools 绑给 LLM → Agent
        from langchain_deepseek import ChatDeepSeek
        from langchain.agents import create_agent
        agent = create_agent(ChatDeepSeek(), tools)

        # 4. 用户问 "查 E001 员工信息"
        #    LLM 看到 tools 里有 query_employee，自动决定调用它
        result = agent.invoke({"messages": [{"role": "user", "content": "查 E001"}]})
    """
    print("[Agent] MCP 工具接入伪代码见本函数 docstring")


# =================================================================
# 入口
# =================================================================
def main():
    if len(sys.argv) > 1 and sys.argv[1] == "--server":
        # 作为 MCP Server 运行
        mcp = build_server()
        print("[Server] MCP Server 启动，暴露工具：query_employee / list_pending_approvals")
        mcp.run(transport="stdio")  # stdio 是 MCP 默认传输
    else:
        # 默认作为 Client 演示
        import asyncio
        print("=" * 55)
        print("MCP Server + Client 演示")
        print("=" * 55)
        asyncio.run(run_client())
        print("\n---")
        demo_agent_with_mcp()


if __name__ == "__main__":
    main()
