# 文件类型

该插件在 IntelliJ 平台内注册了两种专用文件类型。每种文件类型都有其自己的图标、语言基础设施和编辑器功能集。

![Inno Setup 文件类型](assets/images/filetypes.png)

---

## 概览

| 文件类型                   | 扩展名    | 用途                                                         |
|----------------------------|-----------|--------------------------------------------------------------|
| **Inno Setup 脚本**        | `.iss`    | 主安装程序定义 — 节、文件、注册表、代码                      |
| **Inno Setup 语言文件**    | `.isl`    | 单一语言的翻译消息和区域选项                                 |

两种文件类型都通过扩展名自动识别，无需手动关联。

---

## Inno Setup 脚本（`.iss`）

`.iss` 文件是 Inno Setup 的主要格式。它描述完整的安装程序：打包哪些文件、写入哪些注册表项、提供哪些语言，以及——可选地——`[Code]` 节中的 Pascal 脚本运行时逻辑。ISPP 预处理器指令（`#define`、`#include`…）可出现在文件顶部。

有关支持的节和编辑功能的完整列表，请参阅[脚本文件](script-files.md)。

---

## Inno Setup 语言文件（`.isl`）

`.isl` 文件为单个区域提供翻译字符串。它们通过 `[Languages]` 中的 `MessagesFile:` 参数从 `.iss` 脚本引用，并可覆盖内置 Inno Setup 消息的任意子集。项目特定的自定义消息也可以放在 `[CustomMessages]` 中。

有关支持的节和编辑功能的完整列表，请参阅[语言文件](language-files.md)。

---

## 文件类型之间的关系

`.iss` 脚本可通过其 `[Languages]` 节引用一个或多个 `.isl` 文件：

```ini
[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german";  MessagesFile: "compiler:Languages\German.isl"
```

该插件跨文件解析这些引用：在 `[Languages]` 中声明的语言名称是 `[Messages]` 和 `[CustomMessages]` 中语言前缀引用（如 `german.WelcomeLabel1`）的目标，也是脚本中各处值内 `{cm:…}` 常量的目标。
