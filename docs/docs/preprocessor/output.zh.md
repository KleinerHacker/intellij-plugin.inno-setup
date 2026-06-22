# `#emit` / `#expr` / `#insert` / `#append`

这些指令计算表达式并决定如何处理其结果。它们是内联 `{#…}` 形式的显式对应物。

---

## 语法

```ini
#emit Expression
#expr Expression
#insert Expression
#append Expression
```

---

## 说明

- **`#emit`** 计算表达式并将其值作为一行脚本写入输出。普通行内的内联形式 `{#expr}` 是 `{#emit expr}` 的
  简写。
- **`#expr`** 仅为其**副作用**计算表达式（例如调用函数或给宏赋值）并丢弃结果 —— 不输出任何内容。
- **`#insert`** 和 **`#append`** 将输出放置在相对于当前输出行的所选位置 —— `#insert` 在其前，`#append`
  在其后 —— 在以编程方式生成节时很有用。

```ini
#define AppExe "MyApp.exe"

[Run]
#emit "Filename: ""{app}\\" + AppExe + """; Flags: nowait"

; 为副作用计算，不输出任何内容
#expr Local[0] = GetEnv("BUILD_ID")
```

---

## 编辑器支持

四个指令关键字都会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证；表达式会被解析和类型检查。

---

参阅官方 [`#emit` / `#expr` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_emit.htm)。
