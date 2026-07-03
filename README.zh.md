<p align="center">
  <img src="docs/docs/assets/images/inno-setup-logo.png" alt="Inno Setup Logo" width="256"/>
</p>

<p align="center">
  <a href="README.md">English</a> ·
  <b>简体中文</b> ·
  <a href="README.ja.md">日本語</a> ·
  <a href="README.ko.md">한국어</a>
</p>

# Inno Setup – JetBrains 插件

一款 JetBrains IDE 插件，为整个 IntelliJ 平台家族带来对 [Inno Setup](https://jrsoftware.org/isinfo.php)
脚本（`.iss`）的一流语言支持。

---

## 关于

[Inno Setup](https://jrsoftware.org/isinfo.php) 是由 Jordan Russell 和 Martijn Laan 开发的一款广泛使用的免费
Windows 安装程序构建工具（首次发布于 1997 年）。它的脚本（`.iss`）描述了完整的安装程序——文件、注册表项、快捷方式
以及可选的 Pascal 脚本——但在此之前，JetBrains IDE 中一直没有专门的编辑器支持。

本插件填补了这一空白。目标是为 `.iss` 文件提供完整的编辑体验：正确的语法高亮、上下文感知的自动补全、内联文档
以及经过验证的引用，无论你使用哪款 JetBrains IDE。

### 功能特性

| 功能               | 说明                                                                                                          |
|--------------------|---------------------------------------------------------------------------------------------------------------|
| **语法高亮**       | 节、指令、参数、常量（`{app}`、`{autopf}`……）和 Pascal 代码块以不同颜色区分显示                                |
| **代码补全**       | 在输入时自动建议节名、指令键、参数键和已知的标志值                                                             |
| **内联文档**       | 将鼠标悬停在任意指令或参数上，即可在不离开 IDE 的情况下阅读其说明                                              |
| **引用解析**       | 在 `Name:` 声明与其在 `Tasks:`、`Components:` 和 `Types:` 参数中的用法之间导航                                 |
| **结构视图**       | 鸟瞰所有节及其条目                                                                                            |
| **常量校验**       | 识别并校验内置常量，包括嵌入在带引号字符串中的常量                                                            |
| **括号/引号匹配**  | 自动闭合 `{`、`[` 和 `"`                                                                                       |
| **代码折叠**       | 节、较长的参数条目以及 `#if … #endif` 块可独立折叠                                                            |
| **内联提示**       | 在 `Languages:` 参数值旁边内联显示语言标志图标                                                                |
| **构建集成**       | 通过右键菜单操作直接编译 `.iss` 脚本；可选择在项目构建时自动运行 ISCC                                          |
| **语言文件支持**   | `.isl` 语言文件与 `.iss` 脚本一同被识别、高亮和校验                                                           |
| **ISPP 支持**      | 预处理器指令（带作用域关键字的 `#define`/`#undef`、`#include`、`#if`/`#elif`/`#else`/`#endif`……）会被解析、高亮、补全、校验和引用解析 |

### IDE 兼容性

本插件面向 `com.intellij.modules.lang`——在每一款完整的 IntelliJ 平台 IDE 中均可用——并自带其运行时依赖，
因此对宿主 IDE 没有隐藏的要求。

适用于：**IntelliJ IDEA**、**PyCharm**、**CLion / CLion Nova**、**Rider**、**WebStorm**、**GoLand**、**RubyMine**、
**DataGrip** 以及所有其他 IntelliJ 平台 IDE。

---

## 快速开始（开发）

### 前置条件

| 工具          | 版本                                     |
|---------------|------------------------------------------|
| JDK           | 21 或更高                                |
| IntelliJ IDEA | 2024.1 或更高（用于 IDE 辅助开发）       |
| Gradle        | 通过 Gradle Wrapper 提供——无需安装       |

### 构建

```bash
# 克隆仓库
git clone https://github.com/KleinerHacker/inno-setup.git
cd inno-setup

# 生成解析器/词法分析器并编译所有模块
./gradlew assemble

# 运行所有测试（位于 :plugin 模块中）
./gradlew :plugin:test

# 构建可分发的插件 ZIP
./gradlew :plugin:buildPlugin
# → plugin/build/distributions/inno-setup-<version>.zip
```

### 在沙箱 IDE 中运行

```bash
./gradlew runIde
```

这会启动一个加载了本插件的全新 IntelliJ IDEA 实例，与你的常规 IDE 安装相互隔离。打开或创建任意 `.iss`
文件即可实时体验插件。

### 从 IntelliJ IDEA 运行 / 调试

`.run/` 中包含了预配置的运行配置：

| 配置                  | 作用                                                     |
|-----------------------|----------------------------------------------------------|
| **Run Plugin**        | 启动 `:runIde`——打开加载了插件的沙箱 IDE                 |
| **Run Tests**         | 运行 `:test`                                             |
| **Run Verifications** | 运行 `:verifyPlugin` 以检查兼容性                        |

### 项目结构

一个 **Gradle 多模块** 构建，依赖链为 `:plugin → :language:script → :language:preprocessor`。
根项目是一个纯聚合器（没有代码，没有 `plugin.xml`）。

```
.
├── language/
│   ├── preprocessor/        ISPP 预处理器语言（词法/语法/PSI、高亮、注解器、
│   │                        括号匹配、引用、表达式引擎、ISPP 规范、PluginBundle）
│   │   └── src/main/{kotlin, resources/{META-INF, parsing, spec, messages}}
│   └── script/              Inno Setup 语言：节/INI 语法（.iss/.isl/.ist）、文件类型、
│       │                    高亮、折叠、注解器、引用、include 基础设施、ISPP 注入器、
│       │                    spec/settings 服务
│       └── src/main/{kotlin, resources/{META-INF, parsing, spec, icons}}
├── plugin/                  可发布的插件：IDE 功能、构建/运行、设置 UI、主 plugin.xml、
│   │                        配色方案、图标——以及所有测试
│   └── src/{main, test}/
├── buildSrc/                共享的 Gradle 约定（inno-setup.platform-module）
├── <module>/build/generated/  按模块生成的解析器/词法分析器/PSI（自动生成）
├── docs/                    MkDocs 文档站点
├── build.gradle.kts         根聚合器（跨所有模块的 Dokka、kover 合并、MkDocs、generateSources）
└── settings.gradle.kts
```

> **注意：** 生成的源代码按模块存放于 `<module>/build/generated/`。通过 `./gradlew generateSources`
> （根聚合任务）或按模块的 `generateIs*Parser`/`generateIs*Lexer` 任务重新生成。
> 切勿手动编辑——它们会在每次构建时被覆盖。

---

## 手动安装

本插件 **尚未在 JetBrains Marketplace 上架**。请从构建好的 ZIP 手动安装：

### 步骤 1 — 构建插件 ZIP

```bash
./gradlew buildPlugin
```

输出会写入 `build/distributions/inno-setup-<version>.zip`。

### 步骤 2 — 在你的 IDE 中安装

1. 打开你的 JetBrains IDE，进入 **Settings / Preferences → Plugins**
2. 点击 Plugins 面板右上角的 **⚙ 齿轮图标**
3. 选择 **Install Plugin from Disk…**
4. 导航到 `build/distributions/` 并选择该 `.zip` 文件
5. 点击 **OK**，然后在提示时 **重启 IDE**

重启后，任何 `.iss` 扩展名的文件都会被插件自动处理。

---

## 文档

[完整文档](https://kleinerhacker.github.io/intellij-plugin.inno-setup/)——包括每个 Inno Setup 节及其参数的
完整参考——托管在项目的 MkDocs 站点上，由 GitHub Pages 提供服务。

在本地运行文档站点：

```bash
# 安装依赖（一次即可）
cd docs
pip install mkdocs mkdocs-material

# 本地服务
mkdocs serve
```

然后在浏览器中打开 [http://127.0.0.1:8000](http://127.0.0.1:8000)。

> [API 文档](https://kleinerhacker.github.io/intellij-plugin.inno-setup/dokka/html/) 也可用。

---

## 贡献

欢迎[提交 Bug 报告](https://github.com/KleinerHacker/intellij-plugin.inno-setup/issues)
和[拉取请求](https://github.com/KleinerHacker/intellij-plugin.inno-setup/pulls)。对于较大的改动，请先创建 issue
进行讨论。

---

## 许可证

详情请参阅 [LICENSES](https://kleinerhacker.github.io/intellij-plugin.inno-setup/licences/)。
