# `#pragma`

`#pragma` 控制预处理器本身。它不产生输出，而是接受一个**子命令**来调整预处理器读取、解析或报告脚本的
方式。插件从其内置规范中了解每个子命令，并验证其后的参数。

---

## 语法

```ini
#pragma <sub-command> [arguments]
```

`#pragma` 之后的第一个单词是子命令，其后的所有内容是它的参数。未知的子命令会被标记为错误，缺失或类型
不匹配的参数也是如此。

---

## 子命令

| 子命令            | 参数       | 用途                    |
|----------------|----------|-----------------------|
| `option`       | 选项标志     | 预处理器的通用读取/输出选项        |
| `parseroption` | 选项标志     | 控制表达式解析的选项            |
| `message`      | 字符串      | 向编译器窗口打印信息消息          |
| `warning`      | 字符串      | 向编译器窗口打印警告            |
| `error`        | 字符串      | 在对话框中显示错误消息           |
| `verboselevel` | 整数（0–10） | 设置消息的详细程度阈值           |
| `inlinestart`  | 字符串      | 设置内联指令的起始分隔符（默认 `{#`） |
| `inlineend`    | 字符串      | 设置内联指令的结束分隔符（默认 `}`）  |
| `include`      | 字符串      | 设置包含文件的以分号分隔的搜索路径     |
| `spansymbol`   | 字符串      | 设置行续接字符（仅第一个字符）       |

---

## 选项标志：`option` 和 `parseroption`

`option` 和 `parseroption` 接受一个或多个 `-<letter>(+|-)` 形式的标志，以空格分隔。`+` 开启选项，`-`
关闭选项。插件会将未知字母或格式错误的标志（缺少短横线或符号）报告为错误。

### `option` 标志

| 标志  | 默认 | 含义     |
|-----|----|--------|
| `c` | 开  | 输出到编译器 |
| `e` | 开  | 输出空行   |
| `v` | 关  | 详细模式   |

### `parseroption` 标志

| 标志  | 默认 | 含义              |
|-----|----|-----------------|
| `b` | 开  | 布尔短路求值          |
| `m` | 关  | 乘法短路求值          |
| `p` | 开  | Pascal 风格字符串字面量 |
| `u` | 关  | 允许未声明的标识符       |

```ini
#pragma option -v+            ; 启用详细输出
#pragma parseroption -b- -u+  ; 禁用布尔短路，允许未声明的标识符
```

---

## 表达式子命令

`message`、`warning`、`error`、`include`、`inlinestart`、`inlineend` 和 `spansymbol` 接受**字符串
表达式**；`verboselevel` 接受**整数表达式**。插件使用与 `#define` 相同的引擎解析并类型检查参数，因此
错误的类型（例如在需要字符串处使用数字）会被标记，且 `verboselevel` 必须在 0–10 范围内。

```ini
#define BuildId 42
#pragma message "Building configuration #" + Str(BuildId)
#pragma verboselevel 9
#pragma inlinestart "$("
#pragma inlineend ")"
```

这些表达式中的标识符像在 `#define` 值中一样引用你的 `#define` —— 它们可解析、支持转到定义、查找用法
和重命名，未知名称会被标记为未解析的引用。

---

## 编辑器支持

- 对子命令名称、选项标志字母与形式以及参数类型的**验证**
- 在 `#pragma ` 之后**补全**子命令，在 `#pragma option `/`#pragma parseroption ` 之后补全选项标志
- 对表达式参数中使用的 `#define` 进行**引用解析、查找用法和重命名**

---

完整参考请参阅官方
[`#pragma` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_pragma.htm)。
