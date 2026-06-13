# [Messages]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=messagessection){ .md-button .md-button--primary }

`[Messages]` 节覆盖来自 `Default.isl` 或所选语言文件的内置安装程序 UI 字符串。每个条目使用指令语法：

```ini
MessageID=Text
german.MessageID=Text
```

可选的语言前缀针对 `[Languages]` 中声明的单个语言。

---

## 已知消息 ID

该插件内置标准 `Default.isl` 消息标识符作为已知键。补全会建议消息 ID，并在点之前使用时建议可用的语言前缀。

---

## 语言前缀

语言前缀解析到 `[Languages]` 条目。当目标语言可以解析时，插件支持这些前缀的补全、导航、查找用法、重命名和语言国旗内嵌提示。

---

## 值

`string`

Setup 或 Uninstall 显示的消息文本。覆盖期望运行时替换的消息时，请保留 `%1` 和 `%2` 等占位符。
