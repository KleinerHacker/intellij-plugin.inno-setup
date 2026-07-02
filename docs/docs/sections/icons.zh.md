# [Icons]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=iconssection){ .md-button
.md-button--primary }

`[Icons]` 节在安装期间创建 Windows 快捷方式——在开始菜单、桌面或任何其他位置。每个条目恰好创建一个快捷方式。目标可以是可执行文件、文档、文件夹或
URL。使用 `Tasks` 参数使快捷方式可选，让用户在*选择附加任务*向导页面上决定。

---

## Name

`string` · **必需**

快捷方式的完整路径和名称，例如 `{group}\My Program` 或 `{commondesktop}\My Program`。

---

## Filename

`string` · **必需**

快捷方式的目标——可执行文件、文档、文件夹或 URL。

---

## Parameters

`string`

激活快捷方式时传递给目标的命令行参数。

---

## WorkingDir

`string`

启动快捷方式时设置的工作目录。默认为包含目标的目录。

---

## HotKey

`string`

启动目标的全局键盘快捷键，例如 `ctrl+alt+k`。

---

## Comment

`string`

用户悬停在快捷方式上时显示的工具提示文本。

---

## IconFilename

`string`

包含此快捷方式图标的 `.ico`、`.exe` 或 `.dll` 的路径。

---

## IconIndex

`integer`

`IconFilename` 中图标的从零开始的索引。默认为 `0`。

---

## AppUserModelID

`string`

Windows 7+ 应用程序用户模型 ID，用于分组任务栏按钮和关联 toast 通知。

---

## AppUserModelToastActivatorCLSID

`string` · **自 6.1**

Windows 10+ COM CLSID，用于通过此快捷方式激活 toast 通知。

---

## Flags

`string` · **多个值**

行为标志：`runminimized`、`runmaximized`、`closeonexit`、`createonlyiffileexists`、`preventpinning`。

---

## Components

`→ Components` · **多个值**

仅当选择了至少一个列出的组件时，才处理此条目。

---

## Tasks

`→ Tasks` · **多个值**

仅当选中了至少一个列出的任务时，才处理此条目。

---

## Languages

`→ Languages` · **多个值**

将此条目限制为指定的语言。

---

## Check

`string`

`[Code]` 中返回 `Boolean` 的 Pascal 函数名称。仅当函数返回 `True` 时才处理此条目。

---

## BeforeInstall

`string`

创建此快捷方式之前立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## AfterInstall

`string`

创建此快捷方式之后立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## MinVersion

`string`

此条目适用的最低 Windows 版本。使用 `0` 表示从不适用。

---

## OnlyBelowVersion

`string`

此条目适用的最高 Windows 版本（不含）。使用 `0` 表示没有上限。
