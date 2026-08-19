"""测试集（M1 初稿骨架，M6 定稿）。

结构：{question, gold_answer, expect_tool, domain, keywords}
- keywords: 断言用关键词（answer 必须包含其中大部分；离线测试断言召回上下文包含）
- expect_tool: 期望 Agent 调用的工具名；"none" 表示期望不调工具（直接回答/未找到）
- ⚠️ T4 为知识库外问题（防幻觉一票否决项）：知识语料中刻意不含食堂/班车内容

后续实施 Agent 注意：
1. 跑 test_rag_offline.py 时只用制度类用例（expect_tool == "search_policy"）验证检索命中；
2. 跑 test_scenarios.py 时断言 keywords 大部分出现在 answer 且 tool_calls 匹配 expect_tool；
3. 可按需扩充，但 T4 类（知识库外）至少保留 2 条。
"""

TESTCASES = [
    # --- 制度类（走 search_policy）---
    {
        "id": "T1",
        "question": "出差住宿每晚最多能报多少？",
        "gold_answer": "一线城市每晚上限 500 元，二线城市 350 元",
        "keywords": ["500", "350"],
        "expect_tool": "search_policy",
        "domain": "finance/travel",
    },
    {
        "id": "T2",
        "question": "我入职 5 年了，有几天年假？",
        "gold_answer": "满 3 年不满 10 年，年假 10 天/年",
        "keywords": ["10"],
        "expect_tool": "search_policy",
        "domain": "hr/leave",
    },
    {
        "id": "T3",
        "question": "事假一个月最多请几天？",
        "gold_answer": "每月事假不超过 3 天，全年不超过 15 天",
        "keywords": ["3", "15"],
        "expect_tool": "search_policy",
        "domain": "hr/leave",
    },
    {
        "id": "T4",
        "question": "公司食堂几点开饭？",
        "gold_answer": "知识库中未找到相关内容",
        "keywords": ["未找到"],
        "expect_tool": "search_policy",   # 允许检索，但回答必须是"未找到"
        "domain": "out_of_scope",
    },
    {
        "id": "T5",
        "question": "报销单多久之内要提交？",
        "gold_answer": "费用发生后 30 天内提交，逾期不予报销",
        "keywords": ["30"],
        "expect_tool": "search_policy",
        "domain": "finance/expense",
    },
    {
        "id": "T6",
        "question": "报销 2000 块需要谁审批？",
        "gold_answer": "500-3000 元由部门经理审批",
        "keywords": ["部门经理"],
        "expect_tool": "search_policy",
        "domain": "finance/expense",
    },
    {
        "id": "T7",
        "question": "VPN 连不上怎么办？",
        "gold_answer": "先检查 OTP 是否同步，仍失败则提交 ITSM 工单",
        "keywords": ["OTP", "工单"],
        "expect_tool": "search_policy",
        "domain": "admin/it",
    },
    {
        "id": "T8",
        "question": "病假需要什么证明？",
        "gold_answer": "连续病假 2 天及以上需三甲医院证明",
        "keywords": ["三甲"],
        "expect_tool": "search_policy",
        "domain": "hr/leave",
    },
    # --- 工具类（走业务工具）---
    {
        "id": "T9",
        "question": "查一下工号 E001 的员工信息",
        "gold_answer": "张三 / 研发部 / 高级工程师（以 mock_data 为准）",
        "keywords": ["张三"],
        "expect_tool": "query_employee_db",
        "domain": "tool/employee",
    },
    {
        "id": "T10",
        "question": "报销单 RB2024001 现在是什么状态？",
        "gold_answer": "审批中（以 mock_data 为准）",
        "keywords": ["审批"],
        "expect_tool": "query_ticket_status",
        "domain": "tool/ticket",
    },
    {
        "id": "T11",
        "question": "班车路线有哪些？",
        "gold_answer": "知识库中未找到相关内容",
        "keywords": ["未找到"],
        "expect_tool": "search_policy",
        "domain": "out_of_scope",
    },
    {
        "id": "T12",   # stretch：档位 + 档位推理（S6 场景，M6 定稿时启用）
        "question": "E001 入职几年了，按制度年假有几天？",
        "gold_answer": "按 hire_date 推算工龄档位后给年假天数（组合两个工具）",
        "keywords": ["年假"],
        "expect_tool": "query_employee_db+search_policy",
        "domain": "combo",
    },
]
