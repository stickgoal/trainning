# HumanInTheLoop 改造前后对比

本目录用于对照「生产级改造前 / 改造后」两个版本的代码。

## 版本来源
- **改造前（before）**：git tag `hitl-before-refactor`（指向 commit `e748830`）。
  实体化源码在 `compare/humanintheloop/before/`，共 6 个文件：
  - `HumanApprovalWorkflow.java`（改造后被删除）
  - `service/HumanInTheLoopService.java`
  - `controller/HumanInTheLoopController.java`
  - `AgenticDemoApplication.java`
  - `PLAN.md`
  - `pom.xml`
- **改造后（after）**：当前工作区 `src/main/java/com/example/agentic/humanintheloop/...`
  （已提交为 commit `6d1094f`）。

## 查看方式
1. **可视化并排对比（推荐）**：直接打开 `humanintheloop-diff.html`，
   左侧=改造前，右侧=改造后，红/绿/黄分别表示删除/新增/修改。
   生成脚本：`gen_diff.py`（基于 Python 标准库 `difflib.HtmlDiff`）。
2. **命令行 diff**：
   ```bash
   git diff hitl-before-refactor HEAD -- \
     ai/langchain4j-agentic/src/main/java/com/example/agentic/humanintheloop
   ```
3. **IDE 对照**：把 `before/` 目录与 `src/` 对应文件用编辑器的「对比文件」功能打开。

## 回退说明
- 本目录仅为对照用途，不影响业务代码。如需撤销本次对比产物：
  `git rm -r compare && git commit --amend` 或在回退提交时一并丢弃。
- 仅回退业务改造（保留 tag 与对比）：`git revert 6d1094f`。
- 查看改造前完整代码库快照：`git checkout hitl-before-refactor`（ detached HEAD，看完 `git checkout master` 即可）。
