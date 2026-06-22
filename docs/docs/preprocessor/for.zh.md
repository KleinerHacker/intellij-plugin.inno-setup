# `#for`

`#for` 计算一个 C 风格的循环头并多次重复执行一条指令。这是预处理器生成重复脚本内容的方式 —— 例如为
[数组](arrays.md)中的每个项生成一个条目。

---

## 语法

```ini
#for {Init; Condition; Increment} Directive
```

循环头由分号分隔的三部分组成：初始化、每次迭代前检查的条件，以及每次迭代后求值的递增 —— 与 C 的 `for`
循环完全一样。

---

## 说明

每次迭代都会执行尾随的指令。与 [`#emit`](output.md) 或[数组](arrays.md)结合，`#for` 可生成一系列脚本行：

```ini
#dim Langs[3]
#define Langs[0] "en"
#define Langs[1] "de"
#define Langs[2] "fr"

[Languages]
#for {i = 0; i < 3; i++} \
  #emit "Name: """ + Langs[i] + """; MessagesFile: ""compiler:Languages\\" + Langs[i] + ".isl"""
```

反斜杠将指令续接到下一行（更改续接字符请参阅 [`#pragma spansymbol`](pragma.md)）。

---

## 编辑器支持

指令关键字会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证。

---

参阅官方 [`#for` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_for.htm)。
