"""集成测试：通过 TestClient 打完整 HTTP 请求。

TestClient 直接在进程内调用 ASGI 应用（不监听端口），
但中间件、依赖注入、异常处理器、路由全都会真实执行 —— 是性价比最高的测试层级。

对应 Java：@SpringBootTest(webEnvironment = MOCK) + MockMvc。
"""
from fastapi.testclient import TestClient


# ---------------- 健康检查 ----------------


def test_root(client: TestClient):
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["docs"] == "/docs"


def test_health(client: TestClient):
    response = client.get("/health")
    assert response.status_code == 200
    body = response.json()
    assert body["code"] == 0
    assert body["data"]["status"] == "ok"


def test_readiness_counts_tasks(seeded_client: TestClient):
    response = seeded_client.get("/health/ready")
    assert response.status_code == 200
    assert response.json()["data"]["tasks"] == 5


def test_request_id_header_is_injected(client: TestClient):
    """中间件给每个响应都加上 X-Request-ID，便于串联日志。"""
    response = client.get("/health")
    request_id = response.headers.get("x-request-id")
    assert request_id is not None
    assert len(request_id) == 12


# ---------------- 创建 ----------------


def test_create_task_returns_201(client: TestClient):
    response = client.post(
        "/api/v1/tasks",
        json={"title": "修复登录 500", "priority": "urgent", "assignee": "alice",
              "tags": ["backend", "bug"]},
    )
    assert response.status_code == 201, response.text
    body = response.json()
    assert body["code"] == 0
    assert body["data"]["id"]
    assert body["data"]["status"] == "todo"
    assert body["data"]["priority"] == "urgent"


def test_create_task_rejects_missing_title(client: TestClient):
    """参数校验失败：统一的 422 结构（见 core/exceptions.py 的 validation_exception_handler）。"""
    response = client.post("/api/v1/tasks", json={"priority": "high"})
    assert response.status_code == 422
    body = response.json()
    assert body["code"] == 422
    assert body["message"] == "参数校验失败"
    assert body["details"][0]["field"] == "title"


def test_create_task_rejects_unknown_assignee(client: TestClient):
    """业务校验失败：同样是 422，但 message 说的是业务原因，不是"参数校验失败"。"""
    response = client.post("/api/v1/tasks", json={"title": "x", "assignee": "nobody"})
    assert response.status_code == 422
    body = response.json()
    assert body["message"] == "负责人不存在: nobody"
    assert body["details"] == {"assignee": "nobody"}


def test_create_task_rejects_bad_priority(client: TestClient):
    response = client.post("/api/v1/tasks", json={"title": "x", "priority": "super"})
    assert response.status_code == 422
    assert response.json()["message"] == "参数校验失败"


# ---------------- 查询 ----------------


def test_get_task(client: TestClient):
    created = client.post("/api/v1/tasks", json={"title": "任务A"}).json()["data"]
    response = client.get(f"/api/v1/tasks/{created['id']}")
    assert response.status_code == 200
    assert response.json()["data"]["title"] == "任务A"


def test_get_unknown_task_returns_unified_error(client: TestClient):
    response = client.get("/api/v1/tasks/does-not-exist")
    assert response.status_code == 404
    body = response.json()
    assert body["code"] == 404
    assert body["message"] == "任务不存在: does-not-exist"
    assert body["details"]["resource"] == "任务"


def test_unknown_route_also_uses_unified_error(client: TestClient):
    """连框架自己抛的 404 也走统一结构（http_exception_handler）。"""
    response = client.get("/api/v1/no-such-resource")
    assert response.status_code == 404
    assert response.json()["code"] == 404


def test_list_tasks_pagination(seeded_client: TestClient):
    response = seeded_client.get("/api/v1/tasks", params={"page": 2, "page_size": 2})
    assert response.status_code == 200
    data = response.json()["data"]
    assert data["total"] == 5
    assert data["total_pages"] == 3
    assert len(data["items"]) == 2


def test_list_tasks_rejects_out_of_range_page_size(seeded_client: TestClient):
    response = seeded_client.get("/api/v1/tasks", params={"page_size": 999})
    assert response.status_code == 422
    assert response.json()["details"][0]["field"] == "page_size"


def test_list_tasks_filters_by_status(seeded_client: TestClient):
    response = seeded_client.get("/api/v1/tasks", params={"status": "done"})
    data = response.json()["data"]
    assert data["total"] == 1
    assert data["items"][0]["id"] == "task-003"


def test_list_tasks_filters_overdue(seeded_client: TestClient):
    response = seeded_client.get("/api/v1/tasks", params={"only_overdue": "true"})
    data = response.json()["data"]
    assert data["total"] == 1
    assert data["items"][0]["id"] == "task-001"


def test_stats(seeded_client: TestClient):
    response = seeded_client.get("/api/v1/tasks/stats")
    assert response.status_code == 200
    data = response.json()["data"]
    assert data["total"] == 5
    assert data["by_status"]["done"] == 1
    assert data["overdue"] == 1


# ---------------- 更新与流转 ----------------


def test_update_task_partial(client: TestClient):
    created = client.post(
        "/api/v1/tasks", json={"title": "原标题", "description": "原描述", "assignee": "alice"}
    ).json()["data"]

    response = client.patch(f"/api/v1/tasks/{created['id']}", json={"title": "新标题"})
    assert response.status_code == 200
    data = response.json()["data"]
    assert data["title"] == "新标题"
    assert data["description"] == "原描述"      # 未传字段保持原值
    assert data["assignee"] == "alice"


def test_update_empty_body_is_rejected(client: TestClient):
    created = client.post("/api/v1/tasks", json={"title": "x"}).json()["data"]
    response = client.patch(f"/api/v1/tasks/{created['id']}", json={})
    assert response.status_code == 422
    assert "没有可更新的字段" in response.json()["message"]


def test_transition_happy_path(client: TestClient):
    created = client.post("/api/v1/tasks", json={"title": "x"}).json()["data"]

    started = client.post(
        f"/api/v1/tasks/{created['id']}/transition", json={"target": "in_progress"}
    )
    assert started.status_code == 200
    assert started.json()["data"]["status"] == "in_progress"

    done = client.post(f"/api/v1/tasks/{created['id']}/transition", json={"target": "done"})
    assert done.status_code == 200
    assert done.json()["data"]["completed_at"] is not None


def test_transition_conflict_returns_409(client: TestClient):
    created = client.post("/api/v1/tasks", json={"title": "x"}).json()["data"]

    response = client.post(f"/api/v1/tasks/{created['id']}/transition", json={"target": "done"})
    assert response.status_code == 409
    body = response.json()
    assert "不允许的状态流转" in body["message"]
    assert body["details"]["allowed"] == ["cancelled", "in_progress"]


def test_transition_rejects_unknown_status(client: TestClient):
    created = client.post("/api/v1/tasks", json={"title": "x"}).json()["data"]
    response = client.post(
        f"/api/v1/tasks/{created['id']}/transition", json={"target": "archived"}
    )
    assert response.status_code == 422


# ---------------- 删除 ----------------


def test_delete_task_returns_204(client: TestClient):
    created = client.post("/api/v1/tasks", json={"title": "x"}).json()["data"]

    response = client.delete(f"/api/v1/tasks/{created['id']}")
    assert response.status_code == 204
    assert response.content == b""

    assert client.get(f"/api/v1/tasks/{created['id']}").status_code == 404


def test_delete_unknown_task(client: TestClient):
    assert client.delete("/api/v1/tasks/nope").status_code == 404


# ---------------- 文件上传与流式导出 ----------------


def test_import_csv(seeded_client: TestClient):
    csv_bytes = (
        "title,description,priority,assignee,tags,due_date\n"
        "导入任务A,描述,high,alice,backend;bug,2026-03-01\n"
        "导入任务B,,low,bob,,\n"
    ).encode("utf-8")

    response = seeded_client.post(
        "/api/v1/tasks/import",
        files={"file": ("tasks.csv", csv_bytes, "text/csv")},
    )
    assert response.status_code == 200
    data = response.json()["data"]
    assert data["created"] == 2
    assert data["skipped"] == 0
    assert data["errors"] == []


def test_import_rejects_non_csv(seeded_client: TestClient):
    response = seeded_client.post(
        "/api/v1/tasks/import",
        files={"file": ("notes.pdf", b"%PDF-1.4", "application/pdf")},
    )
    assert response.status_code == 422
    assert "只支持" in response.json()["message"]


def test_import_handles_bom(seeded_client: TestClient):
    """Excel 导出的 CSV 带 BOM，首列名会多出 \\ufeff，必须能正常解析。"""
    csv_bytes = "\ufefftitle,priority\n带BOM的任务,high\n".encode("utf-8")
    response = seeded_client.post(
        "/api/v1/tasks/import",
        files={"file": ("bom.csv", csv_bytes, "text/csv")},
    )
    assert response.status_code == 200
    assert response.json()["data"]["created"] == 1


def test_export_stream_returns_sse(seeded_client: TestClient):
    with seeded_client.stream("GET", "/api/v1/tasks/export/stream") as response:
        assert response.status_code == 200
        assert response.headers["content-type"].startswith("text/event-stream")
        body = b"".join(response.iter_bytes()).decode("utf-8")

    assert "event: start" in body
    assert body.count("event: task") == 5
    assert '"count":5' in body.replace(" ", "")


# ---------------- 依赖替换的效果 ----------------


def test_client_uses_empty_repository(client: TestClient):
    """client 夹具覆盖了 get_repository，所以看不到 main.py 里 seed 的演示数据。"""
    response = client.get("/api/v1/tasks")
    assert response.json()["data"]["total"] == 0
