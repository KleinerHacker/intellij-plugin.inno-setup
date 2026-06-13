# [Tasks]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=taskssection){ .md-button .md-button--primary }

`[Tasks]` 节定义用户可以在*选择附加任务*向导页面上启用或禁用的可选操作——例如创建桌面快捷方式或添加右键菜单条目。任务显示为复选框，或在组内标记为 `exclusive` 时显示为单选按钮。其他节中的条目通过其 `Tasks` 参数链接到任务。

---

## Name

`string` · **必需**

此任务的内部标识符。使用反斜杠表示法表示子任务，例如 `desktopicon\user`。

---

## Description

`string` · **必需**

向导中复选框或单选按钮旁边显示的标签。

---

## GroupDescription

`string`

在一组相关任务上方显示的可选标题。

---

## Components

`→ Components` · **多个值**

仅当选择了至少一个列出的组件时，才显示此任务。

---

## Flags

`string` · **多个值**

行为标志：`checkablealone`、`checkedonce`、`dontinheritcheck`、`exclusive`、`restart`、`unchecked`。

---

## Check

`string`

`[Code]` 中返回 `Boolean` 的 Pascal 函数名称。仅当函数返回 `True` 时才处理此条目。

---

## Languages

`→ Languages` · **多个值**

将此条目限制为指定的语言。
