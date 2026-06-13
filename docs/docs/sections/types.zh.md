# [Types]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=typessection){ .md-button .md-button--primary }

`[Types]` 节定义了向导"选择组件"页面上显示的命名安装配置文件——例如*完整*、*精简*和*自定义*。`[Components]` 中的每个组件引用一个或多个类型，以声明默认情况下哪些配置文件包含它。只有一个类型可以使用 `iscustom` 标志标记为用户可自定义类型。

---

## Name

`string` · **必需**

此安装类型的内部标识符。由 `[Components]` 中的 `Types` 参数引用。

---

## Description

`string` · **必需**

向导中此安装类型显示的可读标签。

---

## Flags

`string` · **多个值**

行为标志。`iscustom` 将此类型标记为用户可自定义类型——每个脚本只有一个类型可以携带此标志。
