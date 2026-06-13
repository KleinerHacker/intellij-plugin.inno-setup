# [Run]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=runsection){ .md-button
.md-button--primary }

`[Run]` 节列出了 Inno Setup 在成功安装后、显示最终向导页面之前执行的程序或文件。条目按出现顺序依次运行。您可以在后台静默启动可执行文件、用关联应用程序打开文档，或通过完成页面上的复选框向用户呈现可选的安装后操作（如*启动 MyApp*）。

---

## Filename

`string` · **必需**

要启动的可执行文件、文档或文件夹的路径。使用 `shellexec` 标志以关联应用程序打开非可执行文件。

---

## Description

`string`

安装后完成页面上显示的可选复选框的标签。需要 `postinstall` 标志。

---

## Parameters

`string`

传递给 `Filename` 的命令行参数。

---

## WorkingDir

`string`

启动进程的工作目录。默认为包含 `Filename` 的目录。

---

## StatusMsg

`string`

此条目执行时进度窗口中显示的状态消息。

---

## RunOnceId

`string`

防止此条目在重复安装中运行多次的唯一标识符。

---

## Verb

`string`

与 `shellexec` 标志一起使用的 Shell 动词，例如 `open`、`print`。

---

## OnLog

`string`

为每行输出调用的 `[Code]` 中的 Pascal 过程名称（需要 `logoutput` 标志）。

---

## Flags

`string` · **多个值**

行为标志：`postinstall`、`shellexec`、`nowait`、`runhidden`、`skipifsilent`、`skipifnotsilent`、`unchecked`、`waituntilterminated`、`waituntilidle`、`logoutput`、`runasoriginaluser`。

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

处理此条目之前立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## AfterInstall

`string`

处理此条目之后立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## MinVersion

`string`

此条目适用的最低 Windows 版本。使用 `0` 表示从不适用。

---

## OnlyBelowVersion

`string`

此条目适用的最高 Windows 版本（不含）。使用 `0` 表示没有上限。
