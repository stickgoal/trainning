# TaskFlow

第四章《FastAPI —— 从一个空目录到一个可测试的任务 API》的配套项目。

一个精简但结构完整的团队协作任务 API：内存存储、分层架构、85 个测试全通过。
目的是**演示 FastAPI 工程化骨架**，不是做一个能上线的产品。

## 目录含义

| 目录 | 职责 | Java 类比 |
|------|------|-----------|
| `core/` | 配置、日志、异常体系 | `@ConfigurationProperties` / logback / `@ControllerAdvice` |
| `models/` | Pydantic 模型（入参 / 实体 / 出参） | DTO / Entity / VO |
| `repositories/` | 数据访问接口与实现 | `@Repository` |
| `services/` | 业务规则 | `@Service` |
| `api/` | 路由、依赖注入、中间件 | `@RestController` / `@Bean` / Filter |
| `main.py` | 应用工厂与生命周期 | Spring Boot 启动类 |

## 跑起来

```bash
# 方式一：不安装，直接跑（src 布局需要指定 PYTHONPATH）
PYTHONPATH=src uvicorn taskflow.main:app --reload

# 方式二：先装成可编辑包
uv sync            # 或：pip install -e ".[dev]"
uvicorn taskflow.main:app --reload
```

打开 http://127.0.0.1:8000/docs 查看自动生成的交互式文档。

## 跑测试

```bash
pytest                                   # 85 passed
pytest tests/unit -q                     # 57 passed，毫秒级
pytest tests/integration -q              # 28 passed
pytest --cov=taskflow --cov-report=term-missing   # 覆盖率 97%
```

## 环境变量

复制 `.env.example` 为 `.env` 后按需修改。所有配置项都有默认值，不配也能跑。

## 关于 readme 字段

`pyproject.toml` 里 `readme = "README.md"` 是必需的——构建后端（hatchling）读它生成包元数据。
删掉这个文件，`pip install .` 会直接报 `FileNotFoundError`。
