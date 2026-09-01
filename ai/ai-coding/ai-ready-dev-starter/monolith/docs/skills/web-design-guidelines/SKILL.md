# Web 界面设计规范技能

## When To Use

评审前端 UI 代码、检查可访问性、审查设计、检查 UX、对照最佳实践检查页面时。触发词：「review my UI」「检查可访问性」「审查设计」「检查 UX」「按最佳实践检查页面」。

## Rules

1. **先取最新规范**：每次评审前，用 WebFetch 拉取最新 Web Interface Guidelines：

   ```
   https://raw.githubusercontent.com/vercel-labs/web-interface-guidelines/main/command.md
   ```

2. **读目标文件**：读取指定文件（未指定则先问用户要文件或 glob pattern）。
3. **逐条对照**：按拉取到的规范逐条检查。
4. **输出格式**：按规范中约定的 `file:line` 简洁格式输出发现项。

## 常见审查维度（Vue 3 项目优先关注）

- **可访问性**：语义化标签、`alt` 文本、键盘可操作、焦点管理、对比度。
- **交互**：按钮/链接有可点击面积，hover/focus/active 状态，加载与错误状态。
- **响应式**：移动端布局、触摸目标大小、断点。
- **性能**：图片懒加载、资源体积、无布局抖动。
- **一致性**：与设计系统的颜色/间距/圆角/字号一致。

## Common Mistakes

- **忽略可访问性**：只做视觉，缺键盘操作、缺焦点样式、缺 `alt`。
- **不拉最新规范就评审**：用陈旧标准，遗漏新规则。
- **只给结论不给位置**：不标 `file:line`，前端无法定位。

> 本技能面向 Vue 3 + Element Plus 前端代码；具体规则以 Vercel Web Interface Guidelines 最新版为准。
