# [Components]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=componentssection){
.md-button .md-button--primary }

`[Components]` 节定义向导"选择组件"页面上显示的可选功能。组件可以使用反斜杠表示法组织为父/子层次结构，例如
`extra\plugins`。`[Files]`、`[Icons]` 和其他节中的条目通过其 `Components` 参数链接到组件，因此只有属于所选组件的文件才会被安装。

---

## Name

`string` · **必需**

此组件的内部标识符。使用反斜杠表示法表示层次结构，例如 `main\help`。

---

## Description

`string` · **必需**

在向导的组件选择列表中显示此组件的标签。

---

## Types

`→ Types` · **多个值**

默认包含此组件的安装类型名称（来自 `[Types]`）的空格分隔列表。

---

## ExtraDiskSpaceRequired

`integer`

此组件安装文件之外所需的额外磁盘空间（字节）。显示在选择组件页面上。

---

## Flags

`string` · **多个值**

行为标志：`fixed`、`checkablealone`、`exclusive`、`restart`、`dontinheritcheck`、`disablenouninstallwarning`。

---

## Check

`string`

`[Code]` 中返回 `Boolean` 的 Pascal 函数名称。仅当函数返回 `True` 时才处理此条目。

---

## Languages

`→ Languages` · **多个值**

将此条目限制为指定的语言。
