# [INI]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=inissection){ .md-button
.md-button--primary }

!!! warning "插件支持"
`[INI]` 在当前插件规范中被标记为缺失。此节在这里是为了文档覆盖，但在实现规范之前，补全和验证支持可能不完整。

`[INI]` 节在安装期间创建或更新用户系统上的 `.ini` 文件条目。它对于仍将设置存储在 INI 文件而不是注册表或应用程序数据文件中的遗留应用程序很有用。

---

## Filename

`string` · **必需**

要修改的 `.ini` 文件路径，例如 `{app}\MyApp.ini` 或 `{win}\MyApp.ini`。

---

## Section

`string` · **必需**

包含键的 INI 节名称。

---

## Key

`string`

要创建、更新或删除的 INI 键名称。

---

## String

`string`

写入键的值。

---

## Flags

`string` · **多个值**

行为标志：`createkeyifdoesntexist`、`uninsdeleteentry`、`uninsdeletesection`、`uninsdeletesectionifempty`。

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

## MinVersion

`string`

此条目适用的最低 Windows 版本。使用 `0` 表示从不适用。

---

## OnlyBelowVersion

`string`

此条目适用的最高 Windows 版本（不含）。使用 `0` 表示没有上限。
