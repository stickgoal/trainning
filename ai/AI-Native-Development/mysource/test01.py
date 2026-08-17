import os
from openai import OpenAI

# 1. 从环境变量读 API Key（不要硬编码！）
client = OpenAI(
    api_key=os.getenv("DEEPSEEK_API_KEY"),
    base_url=os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com"),  # 不设默认走 DeepSeek 官方
)

# 2. 发起一次对话补全
response = client.chat.completions.create(
    model=os.getenv("DEEPSEEK_MODEL", "deepseek-chat"),
    messages=[
        {"role": "system", "content": "你是一个简洁的助手。"},
        {"role": "user", "content": "用一句话解释什么是 LLM。"},
    ],
)

# 3. 打印返回
print(response.choices[0].message.content)
print(f"消耗 token: {response.usage.total_tokens}")