# [Files]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=filessection){
.md-button .md-button--primary }

`[Files]` 节是您声明安装程序应复制到目标机器的每个文件的地方。它支持通配符、递归目录树、字体安装、.NET GAC 注册、文件完整性哈希，甚至在安装时从互联网下载文件。列出文件所需的目录会自动创建——无需 `[Dirs]` 条目。

---

## Source

`string` · **必需**

源文件路径或通配符模式，例如 `MyProg.exe` 或 `Plugins\*`。相对于 `SourceDir`。

---

## DestDir

`string` · **必需**

目标机器上的目标目录，例如 `{app}` 或 `{sys}`。支持所有 Inno Setup 常量。

---

## DestName

`string`

在目标机器上重命名文件。如果省略，保留原始文件名。

---

## Excludes

`string`

使用通配符时要排除的文件名模式的逗号分隔列表，例如 `*.pdb,*.log`。

---

## ExternalSize

`integer`

外部文件的大小（字节），用于向导页面上准确的磁盘空间计算（与 `external` 标志一起使用）。

---

## Attribs

`string` · **多个值**

安装文件后设置的文件系统属性：`readonly`、`hidden`、`system`、`notcontentindexed`。

---

## Permissions

`string` · **多个值**

在已安装文件上设置的 ACL 权限，例如 `users-modify`、`everyone-readexec`。

---

## FontInstall

`string`

安装字体文件时使用的注册表字体名称，例如 `My Font (TrueType)`。触发 Windows 中的字体注册。

---

## StrongAssemblyName

`string`

用于全局程序集缓存（GAC）注册的 .NET 强程序集名称。

---

## Hash

`string`

源文件的预期 SHA-256 哈希值。Inno Setup 在编译时验证哈希以捕获意外的文件损坏。

---

## ISSigAllowedKeys

`string` · **多个值**

来自 `[ISSigKeys]` 的用于验证文件 `.issig` 签名的键标识符的逗号分隔列表。

---

## ExtractArchivePassword

`string`

加密存档的密码（与 `extractarchive` 标志一起使用）。以未加密方式存储在安装程序内部。

---

## DownloadISSigSource

`string`

安装时下载的文件的 `.issig` 签名文件的 URL。

---

## DownloadUserName

`string`

用于身份验证文件下载的 HTTP 基本身份验证用户名（需要 `download` 标志）。

---

## DownloadPassword

`string`

用于身份验证文件下载的 HTTP 基本身份验证密码（需要 `download` 标志）。

---

## Flags

`string` · **多个值**

行为标志：`32bit`、`64bit`、`comparetimestamp`、`confirmoverwrite`、`deleteafterinstall`、`dontcopy`、`download`、`external`、`extractarchive`、`ignoreversion`、`isreadme`、`nocompression`、`onlyifdoesntexist`、`recursesubdirs`、`createallsubdirs`、`regserver`、`regtypelib`、`restartreplace`、`sharedfile`。

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

安装此文件之前立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## AfterInstall

`string`

安装此文件之后立即调用的 `[Code]` 中的 Pascal 过程名称。

---

## MinVersion

`string`

此条目适用的最低 Windows 版本。使用 `0` 表示从不适用。

---

## OnlyBelowVersion

`string`

此条目适用的最高 Windows 版本（不含）。使用 `0` 表示没有上限。
