"""模型工厂：一处切换「真模型 / 离线假模型」。

这是整个 ai_demo 项目的枢纽。设计目的：

1. **零门槛运行**：没配 API Key 也能把全部示例跑通，看到真实的数据结构和执行流程。
2. **一行切换**：配好 Key 后，业务代码一行都不用改。
3. **可测试**：假模型返回固定内容，断言才有意义（真模型的输出每次都不一样）。

Java 类比：这就是 Spring 的 `@Bean` + `@Profile`——
开发环境注入 Mock 实现，生产环境注入真实实现，业务代码只依赖接口。
"""
from __future__ import annotations

import os
from collections.abc import Iterator

from langchain_core.language_models import BaseChatModel, FakeListChatModel
from langchain_core.messages import AIMessage, AIMessageChunk
from langchain_core.outputs import ChatGeneration, ChatResult

# ---------------------------------------------------------------------------
# 离线假模型：按调用顺序依次返回下面的内容
# ---------------------------------------------------------------------------
# 注意：这些回复是"剧本"，写死成什么，链就输出什么。
# 这不是作弊——它让我们能在没有网络、没有 Key 的情况下，
# 验证「数据怎么流动」「解析是否生效」「状态机怎么跳转」这些真正要学的部分。
SCRIPTED_REPLIES: list[str] = [
    # 1) 基础链：解释概念
    "LCEL 是 LangChain 的管道语法，用 | 把组件串起来，"
    "前一个的输出就是后一个的输入。",
    # 2) 结构化输出
    '{"summary": "FastAPI 是基于类型注解的现代 Python Web 框架",'
    ' "tags": ["Python", "Web", "FastAPI"], "score": 9}',
    # 3) 工具调用前的思考
    "我需要先查一下这个任务的负责人和截止时间。",
    # 4) 最终回答
    "任务 T-1024 的负责人是 alice，截止 2026-09-10，当前状态为进行中。",
    # 5) LangGraph 路由判断
    "technical",
    # 6) LangGraph 草稿
    "【草稿】FastAPI 通过 Pydantic 模型自动完成请求校验与 OpenAPI 文档生成。",
    # 7) LangGraph 审核意见
    "内容准确，可以发布。",
    # 8) 兜底
    "（离线模式固定回复）",
]


def has_real_key() -> bool:
    """判断是否配置了真实 Key。"""
    return bool(os.getenv("DEEPSEEK_API_KEY") or os.getenv("OPENAI_API_KEY"))


def build_chat_model(
    *,
    model: str = "deepseek-chat",
    temperature: float = 0.0,
    streaming: bool = False,
    scripted: list[str] | None = None,
) -> BaseChatModel:
    """构建聊天模型：有 Key 用真的，没 Key 用假的。

    所有示例都通过这个函数拿模型，于是"能不能联网"这件事被隔离在一个函数里。

    参数 scripted：自定义离线剧本（按顺序返回）。
    离线示例靠它精确控制"模型说什么"，从而演示解析、路由、状态跳转等行为。
    """
    if has_real_key():
        # DeepSeek / OpenAI / 通义 / 智谱 的 OpenAI 兼容模式都走 ChatOpenAI
        from langchain_openai import ChatOpenAI

        base_url = os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com")
        api_key = os.getenv("DEEPSEEK_API_KEY") or os.getenv("OPENAI_API_KEY")
        return ChatOpenAI(
            model=model,
            temperature=temperature,
            streaming=streaming,
            api_key=api_key,
            base_url=base_url,
            timeout=30,
        )

    print("  [离线模式] 未检测到 DEEPSEEK_API_KEY，使用脚本化假模型")

    class _StreamingFake(FakeListChatModel):
        """FakeListChatModel 的流式版本：把整段回复切成小块吐出来。

        真模型的 stream() 是逐 token 返回。为了让学员在无 Key 时
        也能看到"流式"的真实行为（而不是一次性拿到整段），这里手动切块。
        """

        def _call(self, messages, stop=None, run_manager=None, **kwargs) -> str:  # type: ignore[override]
            return super()._call(messages, stop=stop, run_manager=run_manager, **kwargs)

        def stream(self, *args, **kwargs) -> Iterator[AIMessageChunk]:  # type: ignore[override]
            text = self._call(args[0] if args else [])
            for i in range(0, len(text), 4):  # 每 4 个字一块，模拟 token
                yield AIMessageChunk(content=text[i : i + 4])

    return _StreamingFake(responses=list(scripted or SCRIPTED_REPLIES))


class ScriptedChatModel(BaseChatModel):
    """按脚本返回**完整消息**（不只是字符串）的假模型。

    和 FakeListChatModel 的区别：它能返回带 tool_calls 的 AIMessage，
    因此可以用来演示 Agent 的工具调用循环、LangGraph 的条件路由。

    Java 类比：这就是你为 Service 写的 Stub 实现——
    按预设剧本返回，让流程逻辑可测试。
    """

    responses: list[AIMessage] = []
    _cursor: int = 0  # 私有属性，不参与 pydantic 字段

    @property
    def _llm_type(self) -> str:
        return "scripted-chat-model"

    def _generate(self, messages, stop=None, run_manager=None, **kwargs):  # type: ignore[override]
        if self._cursor >= len(self.responses):
            reply = AIMessage(content="（剧本用尽，兜底回复）")
        else:
            reply = self.responses[self._cursor]
        self._cursor += 1
        return ChatResult(generations=[ChatGeneration(message=reply)])

    def bind_tools(self, tools, **kwargs):  # type: ignore[override]
        """Agent 会调用它把工具 schema 绑到模型上。

        假模型不需要真的绑定（它本来就是照剧本演），
        但必须实现这个方法，否则 create_agent 会报
        "model does not support tool calling"。
        """
        return self


def build_scripted_model(messages: list[AIMessage]) -> ScriptedChatModel:
    """构造一个按给定消息序列回复的假模型。"""
    return ScriptedChatModel(responses=list(messages))


def describe_model(llm: BaseChatModel) -> str:
    """返回一行可读的模型说明，示例里打印用。"""
    name = type(llm).__name__
    mode = "真实模型" if has_real_key() else "离线假模型"
    return f"{name}（{mode}）"


if __name__ == "__main__":
    llm = build_chat_model()
    print("模型：", describe_model(llm))
    reply: AIMessage = llm.invoke("说点什么")  # type: ignore[assignment]
    print("类型：", type(reply).__name__)
    print("内容：", reply.content)
    print("usage_metadata：", getattr(reply, "usage_metadata", None))
