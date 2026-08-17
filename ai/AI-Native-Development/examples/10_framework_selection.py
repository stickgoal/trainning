"""
10_framework_selection.py — 主流框架选型与生态进阶（说明性骨架）
对应章节：第11章 框架选型与生态进阶

目的：用一段「零依赖、可直接跑」的代码，演示 CrewAI / AutoGen 这类
「角色化多 Agent 协作」框架的核心思想——把一个任务拆给多个带
「角色 + 目标 + 背景」的 Agent，由 Manager 串联。

真实框架（CrewAI / AutoGen / OpenAI Agents SDK）的 API 见章节正文与下方注释，
本文件刻意不引入任何第三方库，方便你先理解「模式」再学「工具」。
"""

from dataclasses import dataclass, field


@dataclass
class Agent:
    """CrewAI 里 Agent(role=, goal=, backstory=) 的极简等价物"""
    role: str
    goal: str
    backstory: str = ""
    # 用一个假的 "大脑"，真实框架这里换成 LLM 调用
    _memory: list = field(default_factory=list)

    def act(self, task: str) -> str:
        # 真实 CrewAI：LLM 根据 role/goal/backstory + task 生成动作
        result = f"[{self.role}] 针对「{task}」产出：{self.goal}（基于经验：{self.backstory or '无'}）"
        self._memory.append((task, result))
        return result


@dataclass
class Crew:
    """CrewAI 里 Crew(agents=, tasks=) 的极简等价物：顺序协作"""
    name: str
    agents: list
    process: str = "sequential"   # CrewAI 支持 sequential / hierarchical

    def kickoff(self, task: str) -> str:
        print(f"\n=== Crew「{self.name}」启动（process={self.process}）===")
        ctx = task
        for agent in self.agents:
            ctx = agent.act(ctx)          # 上一 Agent 的输出作为下一 Agent 的输入
            print("  " + ctx)
        return ctx


if __name__ == "__main__":
    # 三个角色：研究员 -> 作家 -> 审校，模拟一个内容生产流水线
    researcher = Agent(role="研究员", goal="搜集关键事实", backstory="10 年行业经验")
    writer = Agent(role="作家", goal="把事实写成易懂文章", backstory="前记者")
    reviewer = Agent(role="审校", goal="挑错并给改进建议", backstory="严谨编辑")

    content_crew = Crew("内容生产流水线", [researcher, writer, reviewer])
    final = content_crew.kickoff("写一段关于向量数据库的介绍")
    print("\n最终交付：", final)

# ---------------------------------------------------------------------------
# 真实 CrewAI 写法（需 pip install crewai，并设置 LLM Key），对照上面理解：
#
#   from crewai import Agent, Crew, Task
#   researcher = Agent(role="研究员", goal="搜集关键事实",
#                      backstory="10 年行业经验", llm=llm)
#   writer = Agent(role="作家", goal="写成易懂文章", backstory="前记者", llm=llm)
#   crew = Crew(agents=[researcher, writer],
#               tasks=[Task(description="写向量数据库介绍", agent=researcher),
#                      Task(description="润色成文", agent=writer)],
#               process="sequential")
#   crew.kickoff()
#
# 区别只在「大脑」：本文件用字符串模拟，CrewAI 用真实 LLM 调用。
# ---------------------------------------------------------------------------
