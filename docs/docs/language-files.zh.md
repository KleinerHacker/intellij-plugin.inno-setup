# Inno Setup 语言文件

该插件除了安装程序脚本（`.iss`）外，还支持 Inno Setup 语言文件（`.isl`）。语言文件使用与脚本相同的编辑器基础设施，但只有语言相关的节才有效。

![Inno Setup 语言文件编辑器](assets/images/isl.png)

---

## 支持的节

`.isl` 文件可包含：

- `[LangOptions]` — 语言元数据、区域标识符、字体和文字方向
- `[Messages]` — 翻译的内置安装程序消息
- `[CustomMessages]` — 通过 `{cm:...}` 引用的翻译项目特定消息

`.isl` 文件不需要 `[Setup]`。相反，`[LangOptions]` 必须定义 `LanguageName` 和 `LanguageID`。

---

## 编辑功能

### 语法高亮

`.iss` 文件中使用的相同配色方案也适用于 `.isl` 文件：

- **节标题**（`[LangOptions]`、`[Messages]`、`[CustomMessages]`）作为结构标记高亮显示
- **指令键**及其值以不同颜色显示
- **消息键**与翻译后的字符串值区分显示

### 代码补全

在 `.isl` 文件内提供上下文感知建议：

- 已知 `[LangOptions]` 指令键及其接受的值类型
- `LanguageID` 值附带**国旗图标**、区域名称和十六进制 LCID 提供 — Inno Setup 的内置语言在列表中优先显示

### 内嵌提示

`LanguageID` 值带有匹配的**国旗图标和区域名称**内联注释（如 *Dutch (Netherlands) $0413*），无需在外部文档中查找十六进制标识符即可立即识别语言。

### 内联文档

将鼠标悬停在任意 `[LangOptions]` 键上，显示来自捆绑规范的说明，包括接受值和备注。

### 验证

注解器在 `.isl` 文件中高亮显示问题：

- 未知或拼写错误的 `[LangOptions]` 键
- 无效或无法识别的 `LanguageID` 值
- `[LangOptions]` 中缺少必需指令

### 语言前缀引用

消息键在 `.iss` 脚本中使用时可携带语言前缀，如 `german.WelcomeLabel1`。这些前缀解析到 `[Languages]` 中的 `Name:` 条目，并完全参与：

- **转到定义** — 从前缀跳转到其 `[Languages]` 声明
- **查找用法** — 查找所有引用特定语言名称的位置
- **重命名重构** — 重命名语言名称并一致更新所有带前缀的用法
