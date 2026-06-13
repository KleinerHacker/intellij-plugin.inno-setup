# [Dirs]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=dirssection){
.md-button .md-button--primary }

`[Dirs]` 节在安装期间在目标机器上创建附加目录。大多数情况下根本不需要此节——`[Files]` 中列出的文件所需的目录会自动创建。当您需要创建空目录结构、设置特定的 NTFS 属性或配置目录的 ACL 权限时，请使用 `[Dirs]`。

---

## Name

`string` · **必需**

要创建的目录的完整路径，例如 `{app}\data`。支持所有 Inno Setup 目录常量。

---

## Attribs

`string` · **多个值**

在目录上设置的文件系统属性：`readonly`、`hidden`、`system`、`notcontentindexed`。

---

## Permissions

`string` · **多个值**

在目录上授予的 ACL 权限，例如 `users-modify`、`everyone-readexec`。避免在顶级系统目录（如 `{sys}` 或 `{commonpf}`）上设置 ACL。

---

## Flags

`string` · **多个值**

行为标志：`deleteafterinstall`、`setntfscompression`、`uninsalwaysuninstall`、`uninsneveruninstall`、`unsetntfscompression`。

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

创建此目录之前立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## AfterInstall

`string`

创建此目录之后立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## MinVersion

`string`

此条目适用的最低 Windows 版本。使用 `0` 表示从不适用。

---

## OnlyBelowVersion

`string`

此条目适用的最高 Windows 版本（不含）。使用 `0` 表示没有上限。
