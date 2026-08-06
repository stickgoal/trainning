# 03 - LangChain 组件架构图

```mermaid
graph TB
    subgraph "LangChain 核心架构"
        subgraph "Model Layer 模型层"
            ChatModel[Chat Model<br/>对话模型<br/>OpenAI / Claude / GLM]
            EmbeddingModel[Embedding Model<br/>嵌入模型]
        end

        subgraph "Prompt Layer 提示层"
            PromptTemplate[Prompt Template<br/>提示词模板<br/>变量注入 + 格式化]
            ChatPrompt[Chat Prompt Template<br/>多角色对话模板]
            FewShot[Few-Shot Prompt<br/>少样本示例模板]
        end

        subgraph "Runnable Layer 可运行层 LCEL"
            Runnable[Runnable<br/>统一接口<br/>invoke / stream / batch]
            Chain[Chain<br/>可组合管道<br/>prompt → model → parser]
            Parser[Output Parser<br/>输出解析器<br/>JSON / List / Pydantic]
        end

        subgraph "Retrieval Layer 检索层"
            Retriever[Retriever<br/>检索器接口]
            VectorStore[(Vector Store<br/>向量存储)]
            DocLoader[Document Loader<br/>文档加载器]
            TextSplitter[Text Splitter<br/>文本切分器]
        end

        subgraph "Memory Layer 记忆层"
            BufferMemory[Buffer Memory<br/>完整对话历史]
            SummaryMemory[Summary Memory<br/>摘要记忆]
            VectorMemory[Vector Memory<br/>向量记忆]
        end

        subgraph "Agent Layer 智能体层"
            Agent[Agent<br/>LLM + Tool + 推理循环]
            AgentExecutor[Agent Executor<br/>Agent 执行器]
            Tool[Tool<br/>工具定义]
            Toolkit[Toolkit<br/>工具集合]
        end
    end

    PromptTemplate --> ChatModel
    ChatModel --> Parser
    PromptTemplate --> Chain
    ChatModel --> Chain
    Chain --> Parser

    DocLoader --> TextSplitter
    TextSplitter --> EmbeddingModel
    EmbeddingModel --> VectorStore
    VectorStore --> Retriever
    Retriever --> Chain

    BufferMemory --> Chain
    SummaryMemory --> Chain

    Tool --> Agent
    Toolkit --> Agent
    ChatModel --> Agent
    Agent --> AgentExecutor

    style Chain fill:#e1f5fe
    style Agent fill:#fff3e0
    style Retriever fill:#e8f5e9
    style VectorStore fill:#e8f5e9
```

## LCEL 核心语法

```python
# LCEL (LangChain Expression Language) 声明式链式语法
# 类似 Java Stream API 的管道操作
chain = prompt | model | parser

# 等价于以下 Java 伪代码：
# String result = parser.parse(model.invoke(prompt.format(input)))
```

## 组件职责对照

| LangChain 组件 | 职责 | Java 类比 |
|---------------|------|-----------|
| Chat Model | LLM 调用封装 | HTTP Client |
| Prompt Template | 提示词管理 | 模板引擎 (Thymeleaf) |
| Runnable | 统一执行接口 | Functional Interface |
| Chain | 组件组合管道 | Stream Pipeline |
| Retriever | 知识检索 | Repository / DAO |
| Vector Store | 向量存储 | Elasticsearch Client |
| Memory | 对话状态 | HttpSession |
| Tool | 外部能力 | Service Method |
| Agent | 自主决策 | 状态机 + Service |
