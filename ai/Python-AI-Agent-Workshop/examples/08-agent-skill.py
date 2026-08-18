# -*- coding: utf-8 -*-
"""
示例 08: Agent Skill 能力封装
==============================
展示 Skill 如何将 Agent 能力封装为可复用、可分享的模块

Skill = Procedure（执行流程）+ Prompt（提示词）+ Tool（工具）+ Knowledge（领域知识）
Skill vs Tool：Tool 是单个函数，Skill 是完整的工作流封装
Skill vs Prompt：Prompt 是纯文本，Skill 包含执行逻辑

适合 Java 工程师阅读：Skill 设计模式与 Spring Bean 类比
"""

from typing import TypedDict, Optional, Callable
from dataclasses import dataclass, field
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.tools import tool


# ============================================
# 1. Skill 基础数据结构
# ============================================

@dataclass
class SkillDefinition:
    """
    Skill 定义：描述一个 Agent 技能的完整元数据。
    
    类比 Spring Bean 的 BeanDefinition：
    - name: Bean 名称
    - description: Bean 描述
    - procedure: Bean 的执行方法
    - tools: Bean 依赖的组件
    - knowledge: Bean 的配置属性
    """
    name: str                           # 技能名称（唯一标识）
    description: str                    # 技能描述（用于路由匹配）
    prompt_template: str                # 技能专属 Prompt 模板
    tools: list[Callable] = field(default_factory=list)    # 技能所需工具
    knowledge: str = ""                 # 技能领域知识
    validation_rules: Optional[Callable] = None  # 输出校验规则
    examples: list[str] = field(default_factory=list)     # 少样本示例


# ============================================
# 2. Skill 基类
# ============================================

class BaseSkill:
    """
    Skill 基类：所有具体 Skill 继承此类。
    
    Skill 的核心设计原则：
    1. 单一职责：每个 Skill 聚焦一个领域
    2. 自包含：包含所需 Prompt + Tool + Knowledge
    3. 可组合：Skill 之间可以协作
    4. 可复用：跨 Agent 跨项目复用
    
    Java 类比：abstract class BaseSkill，类似 Spring 的抽象 Bean
    """
    
    def __init__(self, definition: SkillDefinition, llm: ChatOpenAI):
        self.definition = definition
        self.llm = llm
        self.prompt = ChatPromptTemplate.from_messages([
            ("system", definition.prompt_template),
            ("human", "{input}"),
        ])
    
    def execute(self, user_input: str, context: dict = None) -> str:
        """
        执行技能。
        
        子类可以 override 此方法实现自定义逻辑。
        context: 额外上下文信息（如其他 Skill 的结果）
        
        Python override 机制与 Java 相同：子类重写父类方法。
        """
        raise NotImplementedError("子类必须实现 execute 方法")
    
    def get_info(self) -> str:
        """获取技能信息"""
        return f"Skill: {self.definition.name} - {self.definition.description}"


# ============================================
# 3. 具体 Skill 实现
# ============================================

class CodingSkill(BaseSkill):
    """
    编码技能：根据需求生成代码。
    
    包含：
    - 专属 Prompt（编码规范 + 代码风格）
    - 工具（代码执行器、格式化器）
    - 领域知识（编码最佳实践）
    
    类比 Spring Bean：一个注入了 LLM 和工具的 Service。
    """
    
    def __init__(self, llm: ChatOpenAI):
        definition = SkillDefinition(
            name="coding",
            description="根据需求描述生成代码实现",
            prompt_template="""你是一个高级软件工程师。请根据需求生成代码。

要求：
1. 代码简洁清晰，添加适当注释
2. 包含类型提示（Python Type Hints）
3. 处理常见异常情况
4. 遵循 SOLID 原则

{knowledge}""",
            knowledge="语言: Python 3.11+. 框架: FastAPI. 测试: pytest.",
            examples=[
                "需求: 实现用户注册 API → 生成 FastAPI 路由 + Pydantic Model",
                "需求: 实现数据校验 → 生成 Pydantic Validator",
            ],
        )
        super().__init__(definition, llm)
    
    def execute(self, user_input: str, context: dict = None) -> str:
        """执行编码技能"""
        # 构建完整 Prompt（注入领域知识）
        formatted_prompt = self.definition.prompt_template.format(
            knowledge=self.definition.knowledge
        )
        
        response = self.llm.invoke([
            {"role": "system", "content": formatted_prompt},
            {"role": "user", "content": user_input},
        ])
        
        code = response.content
        
        # 校验输出（检查是否包含代码块）
        if "```" not in code:
            code = f"```python\n{code}\n```"
        
        return code


class DatabaseSkill(BaseSkill):
    """
    数据库技能：生成 SQL 查询并执行。
    
    包含：
    - 专属 Prompt（SQL 生成规则 + 安全约束）
    - 工具（SQL 执行器）
    - 领域知识（数据库 Schema 信息）
    """
    
    def __init__(self, llm: ChatOpenAI):
        definition = SkillDefinition(
            name="database",
            description="根据自然语言生成 SQL 查询并执行",
            prompt_template="""你是一个数据库专家。根据自然语言生成 SQL 查询。

规则：
1. 只生成 SELECT 查询（只读操作）
2. 使用参数化查询防止 SQL 注入
3. 添加 LIMIT 防止返回过多数据
4. 使用表别名提高可读性

数据库 Schema:
{knowledge}""",
            knowledge="""
表: users (id, name, email, department, created_at)
表: orders (id, user_id, amount, status, created_at)
表: products (id, name, price, stock)
""",
        )
        super().__init__(definition, llm)
    
    def execute(self, user_input: str, context: dict = None) -> str:
        """执行数据库技能"""
        formatted_prompt = self.definition.prompt_template.format(
            knowledge=self.definition.knowledge
        )
        
        response = self.llm.invoke([
            {"role": "system", "content": formatted_prompt},
            {"role": "user", "content": user_input},
        ])
        
        sql = response.content
        
        # 实际应用中会调用数据库执行 SQL
        # 这里仅返回生成的 SQL
        return sql


class TestingSkill(BaseSkill):
    """
    测试技能：根据代码生成单元测试。
    
    包含：
    - 专属 Prompt（测试框架 + 覆盖率要求）
    - 工具（测试执行器）
    - 领域知识（pytest 最佳实践）
    """
    
    def __init__(self, llm: ChatOpenAI):
        definition = SkillDefinition(
            name="testing",
            description="根据代码实现生成单元测试",
            prompt_template="""你是一个测试工程师。根据代码生成单元测试。

要求：
1. 使用 pytest 框架
2. 覆盖正常路径和边界情况
3. 使用 mock 隔离外部依赖
4. 测试命名清晰：test_what_condition_expected

{knowledge}""",
            knowledge="框架: pytest. Mock: unittest.mock. 覆盖率要求: >80%.",
        )
        super().__init__(definition, llm)
    
    def execute(self, user_input: str, context: dict = None) -> str:
        """执行测试技能"""
        # context 可以包含来自 CodingSkill 的代码
        code_context = context.get("code", "") if context else ""
        
        formatted_prompt = self.definition.prompt_template.format(
            knowledge=self.definition.knowledge
        )
        
        full_input = f"待测试代码:\n{code_context}\n\n需求: {user_input}"
        
        response = self.llm.invoke([
            {"role": "system", "content": formatted_prompt},
            {"role": "user", "content": full_input},
        ])
        
        return response.content


class DeploymentSkill(BaseSkill):
    """
    部署技能：生成部署配置和 CI/CD 管线。
    
    包含：
    - 专属 Prompt（部署模板 + 安全规范）
    - 工具（Docker / K8s CLI）
    - 领域知识（容器化最佳实践）
    """
    
    def __init__(self, llm: ChatOpenAI):
        definition = SkillDefinition(
            name="deployment",
            description="生成部署配置（Dockerfile / CI/CD 管线）",
            prompt_template="""你是一个 DevOps 工程师。根据项目信息生成部署配置。

要求：
1. 多阶段构建优化镜像大小
2. 非 root 用户运行
3. 健康检查配置
4. 环境变量管理

{knowledge}""",
            knowledge="容器: Docker. 编排: Kubernetes. CI/CD: GitHub Actions.",
        )
        super().__init__(definition, llm)
    
    def execute(self, user_input: str, context: dict = None) -> str:
        """执行部署技能"""
        formatted_prompt = self.definition.prompt_template.format(
            knowledge=self.definition.knowledge
        )
        
        response = self.llm.invoke([
            {"role": "system", "content": formatted_prompt},
            {"role": "user", "content": user_input},
        ])
        
        return response.content


# ============================================
# 4. Skill Router — 技能路由器
# ============================================

class SkillRouter:
    """
    技能路由器：根据用户请求匹配并调度合适的 Skill。
    
    工作流程：
    1. 用户请求进来
    2. 路由器分析请求意图
    3. 匹配到合适的 Skill
    4. 执行 Skill 并返回结果
    
    Java 类比：类似 Spring 的 DispatcherServlet，
    根据请求路由到对应的 Controller。
    """
    
    def __init__(self, llm: ChatOpenAI):
        self.skills: dict[str, BaseSkill] = {}
        self.llm = llm
    
    def register(self, skill: BaseSkill) -> None:
        """注册技能"""
        self.skills[skill.definition.name] = skill
        print(f"已注册技能: {skill.definition.name}")
    
    def route(self, user_input: str) -> str:
        """
        路由用户请求到合适的技能。
        
        实际应用中可以用 LLM + 向量检索来做智能路由。
        这里用关键词匹配做简化演示。
        """
        # 简化的路由逻辑（实际应用中用 LLM 或 Embedding 匹配）
        routing_rules = {
            "coding": ["写代码", "实现", "编码", "开发", "函数", "API"],
            "database": ["查询", "SQL", "数据库", "数据"],
            "testing": ["测试", "test", "pytest", "单元测试"],
            "deployment": ["部署", "deploy", "Docker", "CI/CD", "发布"],
        }
        
        matched_skill = None
        max_score = 0
        
        for skill_name, keywords in routing_rules.items():
            score = sum(1 for kw in keywords if kw in user_input)
            if score > max_score:
                max_score = score
                matched_skill = skill_name
        
        if matched_skill and matched_skill in self.skills:
            return matched_skill
        
        # 如果没有匹配到，用 LLM 判断
        skill_list = ", ".join(self.skills.keys())
        response = self.llm.invoke([
            {"role": "system", "content": f"根据用户请求选择最合适的技能。可选技能: {skill_list}。只返回技能名称。"},
            {"role": "user", "content": user_input},
        ])
        return response.content.strip()
    
    def execute(self, user_input: str) -> dict:
        """执行完整的路由 + 技能调用流程"""
        print(f"\n--- 技能路由 ---")
        print(f"用户请求: {user_input}")
        
        # 步骤 1: 路由匹配
        skill_name = self.route(user_input)
        print(f"匹配技能: {skill_name}")
        
        # 步骤 2: 执行技能
        if skill_name in self.skills:
            skill = self.skills[skill_name]
            result = skill.execute(user_input)
            print(f"技能执行完成: {skill_name}")
            return {
                "skill": skill_name,
                "result": result,
                "success": True,
            }
        else:
            print(f"未找到匹配的技能: {skill_name}")
            return {
                "skill": skill_name,
                "result": "未找到匹配的技能",
                "success": False,
            }


# ============================================
# 5. Skill 组合 — 多技能协作
# ============================================

class SkillOrchestrator:
    """
    技能编排器：将多个 Skill 组合完成复杂任务。
    
    例如：用户说"帮我实现一个用户注册 API 并写测试"
    → 编排器依次调用 CodingSkill → TestingSkill
    
    Java 类比：类似 Spring 的 Service 编排层，
    组合多个 Bean 的方法完成业务逻辑。
    """
    
    def __init__(self, router: SkillRouter):
        self.router = router
    
    def execute_pipeline(self, user_input: str) -> dict:
        """执行多技能管线"""
        results = {}
        
        # 步骤 1: 编码
        if any(kw in user_input for kw in ["实现", "编码", "开发", "写代码"]):
            coding_result = self.router.skills["coding"].execute(user_input)
            results["code"] = coding_result
            
            # 步骤 2: 测试（依赖编码结果）
            if any(kw in user_input for kw in ["测试", "test"]):
                test_result = self.router.skills["testing"].execute(
                    "为上述代码编写单元测试",
                    context={"code": coding_result}
                )
                results["test"] = test_result
            
            # 步骤 3: 部署（可选）
            if any(kw in user_input for kw in ["部署", "deploy"]):
                deploy_result = self.router.skills["deployment"].execute(
                    f"为以下代码生成部署配置:\n{coding_result}"
                )
                results["deploy"] = deploy_result
        
        return results


# ============================================
# 6. 运行 Skill 示例
# ============================================

def main():
    print("=" * 60)
    print("Agent Skill 能力封装示例")
    print("=" * 60)
    
    llm = ChatOpenAI(model="gpt-4o", temperature=0)
    
    # 创建技能路由器
    router = SkillRouter(llm)
    
    # 注册技能
    router.register(CodingSkill(llm))
    router.register(DatabaseSkill(llm))
    router.register(TestingSkill(llm))
    router.register(DeploymentSkill(llm))
    
    # 测试单个技能
    print("\n=== 单技能调用 ===")
    
    result = router.execute("帮我写一个用户注册的 API 函数")
    print(f"\n结果:\n{result['result'][:200]}...")
    
    result = router.execute("查询所有技术部门的用户")
    print(f"\n结果:\n{result['result'][:200]}...")
    
    # 测试技能组合
    print("\n=== 技能组合调用 ===")
    orchestrator = SkillOrchestrator(router)
    results = orchestrator.execute_pipeline("帮我实现一个用户注册 API 并写单元测试")
    
    for skill_name, result in results.items():
        print(f"\n[{skill_name}] 结果:\n{result[:150]}...")
    
    # Skill vs Tool vs Prompt 对比
    print("\n=== Skill vs Tool vs Prompt ===")
    print("""
    ┌──────────┬──────────────────────────────────────────┐
    │  维度     │  说明                                    │
    ├──────────┼──────────────────────────────────────────┤
    │  Prompt  │  纯文本指令，无执行逻辑                    │
    │          │  例: "你是一个编码助手，请帮我写代码"       │
    ├──────────┼──────────────────────────────────────────┤
    │  Tool    │  单个函数，单一操作                       │
    │          │  例: def execute_code(code): ...          │
    ├──────────┼──────────────────────────────────────────┤
    │  Skill   │  完整能力封装：Prompt + Tool + Knowledge   │
    │          │  例: CodingSkill = 编码 Prompt +           │
    │          │       代码执行 Tool + 编码规范 Knowledge    │
    │          │  可复用、可分享、可组合                     │
    └──────────┴──────────────────────────────────────────┘
    """)


if __name__ == "__main__":
    main()
