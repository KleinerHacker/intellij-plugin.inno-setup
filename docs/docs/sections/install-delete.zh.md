# [InstallDelete]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=installdeletesection){
.md-button .md-button--primary }

`[InstallDelete]` 节列出了 Inno Setup 在安装*开始*时、复制任何新文件之前删除的文件和目录。这对于清理应用程序先前版本遗留下来的、新安装程序不再跟踪的过时文件或旧目录结构很有用。

---

## Type

`string` · **必需**

要删除的内容：`files`（仅匹配文件）、`filesandordirs`（文件和所有子目录）、`dirifempty`（仅当目录不包含文件时删除目录）。

---

## Name

`string` · **必需**

要删除的文件或目录的路径或通配符模式，例如 `{app}\*.log` 或 `{app}\cache`。

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
