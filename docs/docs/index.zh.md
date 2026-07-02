# Inno Setup – JetBrains 插件

**在所有 JetBrains IDE 中对 Inno Setup 脚本（`.iss`）和语言文件（`.isl`）提供一流的编辑器支持。**

---

## 什么是 Inno Setup？

[Inno Setup](https://jrsoftware.org/isinfo.php) 是由 Jordan Russell 和 Martijn Laan 创建的免费开源 Windows 安装程序构建工具。自
1997 年首次发布以来，它已成为 Windows 生态系统中使用最广泛的安装程序工具之一——为 **Visual Studio Code**、**Git for Windows
** 和 **Embarcadero Delphi** 等项目的安装程序提供支持。

Inno Setup 脚本（`.iss`）描述完整的安装程序配置：安装哪些文件、创建哪些注册表项、添加哪些快捷方式，以及安装向导的行为方式。语言文件（
`.isl`）提供本地化的向导文本和语言元数据。它们共同支持丰富的节、参数、消息集，以及用于完整运行时自定义的集成 Pascal 脚本引擎。

!!! tip "官方 Inno Setup 资源"

- :octicons-home-16: [主页](https://jrsoftware.org/isinfo.php)
- :octicons-book-16: [文档](https://jrsoftware.org/ishelp/)
- :octicons-download-16: [下载](https://jrsoftware.org/isdl.php)

---

## 此插件的功能

本插件为任意 JetBrains IDE 中的 `.iss` 和 `.isl` 文件带来一流的编辑器支持——包括
**IntelliJ IDEA**、**PyCharm**、**CLion**、**Rider**、**WebStorm**、**GoLand** 及其他：

- **语法高亮** — 节、指令、参数、值、常量和 Pascal 代码块均以不同颜色显示
- **代码补全** — 在输入时提示节标题、参数名和已知值
- **内联文档** — 将鼠标悬停在任意指令或参数上即可查看其说明，无需离开 IDE
- **引用解析** — 在脚本中的组件、任务和类型定义之间导航
- **本地化支持** — 完整支持 `[Messages]`、`[CustomMessages]`、`[LangOptions]`、语言前缀及 `{cm:...}` 引用
- **语言元数据** — Windows LCID 补全、内置 Inno Setup 语言建议和语言引用的国旗内嵌提示
- **结构视图** — 在项目工具窗口中鸟瞰所有节及其条目
- **常量支持** — `{app}`、`{autopf}`、`{group}` 及所有其他内置常量均可识别和验证，包括在引号字符串内部

---

## IDE 兼容性

该插件基于 `com.intellij.modules.lang` 构建，该语言支持模块存在于每个完整的 JetBrains IDE 中。它**不**特别要求 IntelliJ
IDEA，并自带 YAML 解析基础设施，因此对宿主 IDE 没有隐藏的运行时依赖。

| IDE                    | 支持情况 |
|------------------------|------|
| IntelliJ IDEA（社区版和旗舰版） | ✔    |
| PyCharm（社区版和专业版）       | ✔    |
| CLion / CLion Nova     | ✔    |
| Rider                  | ✔    |
| WebStorm               | ✔    |
| GoLand                 | ✔    |
| RubyMine               | ✔    |
| DataGrip               | ✔    |
| 其他 IntelliJ 平台 IDE     | ✔    |

---

## 安装

该插件**尚未在 JetBrains Marketplace 发布**。请从本地构建的 JAR/ZIP 手动安装：

### 1 · 构建插件

```bash
./gradlew buildPlugin
```

可分发的 ZIP 文件将写入 `build/distributions/`。

### 2 · 在 IDE 中安装

1. 打开 **设置 / 首选项 → 插件**
2. 点击 ⚙ 齿轮图标，选择 **从磁盘安装插件…**
3. 从 `build/distributions/` 中选择 ZIP 文件
4. 按提示重启 IDE

!!! note "Marketplace 发布"
计划发布到 JetBrains Marketplace。发布后，可直接从 IDE 内置的插件浏览器安装该插件。

---

## 节参考

上方导航包含 Inno Setup 脚本的节参考，以及专用的 `.isl` 语言文件参考，包括类型信息和官方 Inno Setup 文档链接。
