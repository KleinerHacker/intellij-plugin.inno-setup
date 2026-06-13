# [UninstallDelete]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=uninstalldeletesection){ .md-button .md-button--primary }

`[UninstallDelete]` 节列出了 Inno Setup 在卸载期间（除了它最初跟踪的文件之外）额外删除的文件和目录。使用它来清理在运行时创建的、因此从未在 `[Files]` 中列出的生成文件、缓存、日志文件或用户数据目录。

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
