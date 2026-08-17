"""
============================================================
 00_setup.py — 环境自检 + 第一个 LLM 调用
 对应章节：第1章 Python AI 生态概览
============================================================

【运行流程】
 1. 激活虚拟环境：.venv/Scripts/activate  (Windows)
    或 source .venv/bin/activate (macOS/Linux)
 2. 设置 API Key：set DEEPSEEK_API_KEY=sk-xxx
    （可选）切换兼容端点：
    set DEEPSEEK_BASE_URL=https://api.deepseek.com
    set DEEPSEEK_API_KEY=sk-xxx
 3. 运行：python examples/00_setup.py
 4. 预期输出：环境检查 OK + LLM 一句话回答

【应用场景】
 任何 AI 项目的第一步：确认环境 + 跑通最简调用。
 对 Java 工程师：相当于 Spring Boot 的 "Hello World" 启动类。

【Python 特殊语法提示】
 - from X import Y        : 导入，类似 Java 的 import
 - f"..."                 : f-string 字符串插值，类似 Java 的 String.format
 - if __name__ == "__main": 模块入口，类似 Java 的 public static void main
 - client.chat...create() : 链式属性访问（chat 是属性，completions 是属性）
"""

import os
import sys


def check_python_version() -> None:
    """检查 Python 版本。返回值 None 是类型提示（Java 没有等价物，类似 void）。"""
    major, minor = sys.version_info[:2]
    print(f"[1/4] Python 版本: {major}.{minor}")
    if (major, minor) < (3, 10):
        print("    ⚠️  建议使用 Python 3.10+，部分库（如 LangGraph）要求 3.10+")
    else:
        print("    ✅ 版本满足要求")


def check_api_key() -> str:
    """读取 API Key，找不到就提示。"""
    key = os.getenv("DEEPSEEK_API_KEY", "")
    print(f"[2/4] DEEPSEEK_API_KEY: {'已设置 (' + key[:6] + '...)' if key else '❌ 未设置'}")
    return key


def call_llm_once(question: str) -> str:
    """
    最简 LLM 调用：8 行代码完成一次问答。
    这是整个 Python AI 生态的 "Hello World"。
    """
    # 延迟 import：只在真正用到时才加载库，加速启动（Java 类比：懒加载类）
    from openai import OpenAI

    # 创建客户端：默认指向 DeepSeek 官方端点（OpenAI 兼容接口）
    client = OpenAI(
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        base_url=os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com"),
    )

    # 调用 chat completions 接口
    # messages 是一个 list[dict]，role 有 system/user/assistant 三种
    response = client.chat.completions.create(
        model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
        messages=[
            {"role": "system", "content": "你是一位简洁的技术助手，用一句话回答。"},
            {"role": "user", "content": question},
        ],
        temperature=0.3,  # 0=确定，1=随机；低温度=稳定输出
    )

    # response.choices[0].message.content 是标准取值路径
    return response.choices[0].message.content


def main() -> None:
    print("=" * 50)
    print("Python AI Agent 课程 · 环境自检")
    print("=" * 50)

    check_python_version()
    api_key = check_api_key()
    print(f"[3/4] 当前目录: {os.getcwd()}")
    print(f"[4/4] 依赖检查：请在课前执行 pip install -r requirements")

    if not api_key:
        print("\n⚠️  未检测到 DEEPSEEK_API_KEY，跳过 LLM 调用演示。")
        print("   设置方法：set DEEPSEEK_API_KEY=sk-xxx")
        return

    print("\n" + "-" * 50)
    print("调用 LLM 演示：")
    print("-" * 50)
    answer = call_llm_once("用一句话说明 Python 为什么适合 AI 开发。")
    print(f"问：为什么 Python 适合 AI 开发？")
    print(f"答：{answer}")


if __name__ == "__main__":
    main()
