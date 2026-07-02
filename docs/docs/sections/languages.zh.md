# [Languages]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=languagessection){
.md-button .md-button--primary }

`[Languages]` 节声明安装程序中可用的所有语言。每个条目指向一个 ISL 消息文件，该文件为向导 UI
提供翻译后的字符串。列表中的第一个条目成为默认语言。在此处定义的语言可通过其他节中的 `Languages` 公共参数引用，以将条目限制为特定区域。

---

## Name

`string` · **必需**

内部语言标识符，例如 `english`、`german`。由其他节中的 `Languages` 参数引用。

---

## MessagesFile

`string` · **必需**

ISL 消息文件的路径。使用 `compiler:Default.isl` 获取内置英文消息，或使用 `compiler:Languages\German.isl` 获取捆绑的翻译之一。
