# `#error`

`#error` 立即停止编译并报告给定的消息。用它在不满足必需条件时（例如缺少宏或不受支持的配置）使构建失败。

---

## 语法

```ini
#error Message
```

消息是该行的其余部分。它会显示给用户，并中止编译。

---

## 说明

`#error` 通常由[条件](conditionals.md)保护，使其仅在不良情况下触发：

```ini
#ifndef AppVersion
  #error AppVersion must be defined before including this file
#endif
```

与接受字符串**表达式**的 `#pragma error` 不同，`#error` 将行的其余部分视为纯消息。

---

## 编辑器支持

指令关键字会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证。

---

参阅官方 [`#error` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_error.htm)。
