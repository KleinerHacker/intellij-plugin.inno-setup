# [Registry]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=registrysection){
.md-button .md-button--primary }

`[Registry]` 节在安装期间创建、修改或删除 Windows 注册表键和值。它支持所有主要的注册表值类型，并通过 `uninsdeletekey` 和 `uninsdeletekeyifempty` 等标志对卸载时注册表数据的处理提供精细控制。可以明确定向 32 位和 64 位注册表视图。

---

## Root

`string` · **必需**

注册表根配置单元：`HKCU`、`HKLM`、`HKCR`、`HKU`、`HKCC` 或 `HKA`（自动，取决于安装模式）。附加 `32` 或 `64` 可强制特定注册表视图，例如 `HKLM64`。

---

## Subkey

`string` · **必需**

相对于 `Root` 的注册表键路径，例如 `Software\My Company\My App`。

---

## ValueType

`string`

要写入的注册表值类型：`none`（仅键）、`string`、`expandsz`、`multisz`、`dword`、`qword`、`binary`。

---

## ValueName

`string`

注册表值的名称。留空则以键的默认值为目标。

---

## ValueData

`string`

要写入的数据。使用 `{olddata}` 追加到现有值，或使用 `{break}` 作为 `multisz` 值的行分隔符。

---

## Permissions

`string` · **多个值**

在键上设置的 ACL 权限：`full`、`modify`、`read`。

---

## Flags

`string` · **多个值**

行为标志：`createvalueifdoesntexist`、`deletekey`、`deletevalue`、`dontcreatekey`、`noerror`、`preservestringtype`、`uninsclearvalue`、`uninsdeletekey`、`uninsdeletekeyifempty`、`uninsdeletevalue`。

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
