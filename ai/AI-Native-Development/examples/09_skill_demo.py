"""
============================================================
 09_skill_demo.py — Agent Skill 能力封装
 对应章节：第10章 Agent Skill
============================================================

演示：
 - Skill 基类：Prompt + Tool + 使用说明 + 触发条件
 - 一个 DatabaseSkill 实现
 - Agent 根据 Skill 描述自动选择

【运行流程】
 pip install langchain-deepseek langchain-core
 set DEEPSEEK_API_KEY=sk-xxx
 python examples/09_skill_demo.py
 （不调 LLM 也能跑，会用伪逻辑演示 Skill 选择）

【应用场景】
 把专家经验沉淀成 Agent 可复用能力。
 Tool=方法，Prompt=注释，Skill=带文档的 Spring Bean。

【Python 特殊语法提示】
 - ABC + @abstractmethod  : 抽象类强制实现
 - @property              : 把方法变属性访问（Java 的 getter）
 - Optional[str]         : 可空类型（Java Optional<String>）
 - 字符串前缀 r"..."     : 原始字符串，不转义反斜杠（写正则/SQL 常用）
"""

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Optional, Callable, Any


# =================================================================
# Skill 基类：能力级封装
# =================================================================
@dataclass
class BaseSkill(ABC):
    """
    Skill = Prompt + Tool + 使用说明 + 触发条件

    对比：
      - Tool  ：函数级，只有一个可调用方法
      - Prompt：文本级，只有一段提示词
      - Skill ：能力级，是一个 "带文档的能力包"

    Java 类比：Tool=方法，Prompt=注释，Skill=带 @Component 的 Spring Bean。
    """
    name: str
    description: str              # Skill 干什么用（发给 LLM 让它选）
    prompt_template: str          # 专家提示词模板
    trigger_keywords: list = field(default_factory=list)  # 何时自动激活

    @property
    @abstractmethod
    def tools(self) -> list[Callable[..., Any]]:
        """这个 Skill 包含的工具集。子类必须实现。"""
        ...

    @abstractmethod
    def run(self, task: str) -> str:
        """执行 Skill。子类定义具体逻辑。"""
        ...

    def matches(self, user_input: str) -> bool:
        """触发条件：用户输入命中关键词则激活本 Skill。"""
        return any(kw in user_input for kw in self.trigger_keywords)


# =================================================================
# 具体 Skill 1：数据库 Skill
# =================================================================
class DatabaseSkill(BaseSkill):
    """数据库操作 Skill：写 SQL + 评审 + 执行。"""

    def __init__(self):
        super().__init__(
            name="DatabaseSkill",
            description="当用户需要查询/修改数据库时使用。能写 SQL、做安全评审、执行查询。",
            prompt_template=r"""
你是数据库专家，根据用户需求生成安全的 SQL。
规则：
1. 只允许 SELECT，禁止 DROP / TRUNCATE
2. 必须加 LIMIT
3. 输出格式：```sql\n...\n```

需求：{task}
""".strip(),
            trigger_keywords=["查询", "SQL", "数据库", "表"],
        )

    @property
    def tools(self) -> list:
        def execute_sql(sql: str) -> str:
            """执行 SQL（此处模拟）。"""
            # 真实场景连数据库
            return f"执行结果（模拟）: 5 行数据"

        def review_sql(sql: str) -> str:
            """SQL 安全评审。"""
            danger = ["DROP", "TRUNCATE", "DELETE FROM"]
            if any(d in sql.upper() for d in danger):
                return "❌ 危险 SQL，已拦截"
            return "✅ 通过安全评审"

        return [execute_sql, review_sql]

    def run(self, task: str) -> str:
        # 真实场景：用 prompt_template 调 LLM 生成 SQL
        # 此处用伪逻辑演示 Skill 内部流程
        generated_sql = f"SELECT * FROM orders WHERE status='pending' LIMIT 100; -- 任务: {task}"

        review_tool = self.tools[1]
        execute_tool = self.tools[0]

        review_result = review_tool(generated_sql)
        if "通过" in review_result:
            exec_result = execute_tool(generated_sql)
            return f"[DatabaseSkill] 生成 SQL:\n{generated_sql}\n评审: {review_result}\n{exec_result}"
        return f"[DatabaseSkill] SQL 未通过评审:\n{review_result}"


# =================================================================
# 具体 Skill 2：测试 Skill（简化版）
# =================================================================
class TestingSkill(BaseSkill):
    """测试 Skill：生成用例 + 执行。"""

    def __init__(self):
        super().__init__(
            name="TestingSkill",
            description="当用户需要写测试用例或跑测试时使用。",
            prompt_template="根据函数生成 pytest 用例：{task}",
            trigger_keywords=["测试", "test", "用例"],
        )

    @property
    def tools(self):
        def run_pytest(target: str) -> str:
            return f"测试通过（模拟）：{target}"
        return [run_pytest]

    def run(self, task: str) -> str:
        return f"[TestingSkill] 生成 3 个用例并执行 → 全部通过\n  覆盖: {task}"


# =================================================================
# Agent：根据 Skill 描述自动选能力
# =================================================================
class SkillAgent:
    """
    一个能选 Skill 的 Agent。
    真实实现：把所有 Skill 的 description 发给 LLM，让它选最匹配的。
    此处用关键词匹配做简化版。
    """

    def __init__(self, skills: list[BaseSkill]):
        self.skills = skills

    def select_skill(self, user_input: str) -> Optional[BaseSkill]:
        """
        Skill 选择逻辑。
        真实场景：让 LLM 看所有 Skill description 决定。
        此处：关键词命中。
        """
        for s in self.skills:
            if s.matches(user_input):
                return s
        return None

    def run(self, user_input: str) -> str:
        skill = self.select_skill(user_input)
        if not skill:
            return "没有匹配的 Skill，请补充能力库。"
        return skill.run(user_input)


# =================================================================
# 主流程
# =================================================================
def main() -> None:
    print("=" * 55)
    print("Agent Skill 演示（无需 API Key）")
    print("=" * 55)

    agent = SkillAgent([DatabaseSkill(), TestingSkill()])

    cases = [
        "查询订单表里待处理的记录",
        "给 add 函数写测试用例",
        "帮我画一张架构图",   # 无匹配 Skill
    ]

    for c in cases:
        print(f"\n用户: {c}")
        result = agent.run(c)
        print(f"Agent: {result}")


if __name__ == "__main__":
    main()
