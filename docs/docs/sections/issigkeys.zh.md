# [ISSigKeys]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=issigkeyssection){
.md-button .md-button--primary }

`[ISSigKeys]` 节定义用于验证 `.issig` 文件签名的公钥。这些键由 `[Files]` 中的 `ISSigAllowedKeys` 参数和 `issigverify`
标志引用。

*此节自 Inno Setup 6.5 起可用。*

---

## Name

`string` · **必需**

此键条目的标识符。`[Files]` 条目通过 `ISSigAllowedKeys` 引用它。

---

## Group

`string`

允许多个键在 `ISSigAllowedKeys` 中共享标识符的逻辑组名。

---

## KeyFile

`string`

包含公钥数据的密钥文件路径。

---

## PublicX

`string`

公共 EC 密钥 X 坐标的十六进制编码。

---

## PublicY

`string`

公共 EC 密钥 Y 坐标的十六进制编码。

---

## KeyID

`string`

安装程序中嵌入的可选编译时密钥标识符，用于密钥查找。

---

## RuntimeID

`string`

安装程序在安装时用于密钥查找的可选运行时密钥标识符。
