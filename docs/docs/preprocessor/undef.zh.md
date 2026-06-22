# `#undef`

`#undef` 移除先前用 [`#define`](define.md) 声明的宏。此后该名称不再被定义：`defined(Name)` 变为 false，
之后对该名称的任何使用都被视为未定义。

---

## 语法

```ini
#undef [Scope] Name
```

名称前可加可选的作用域关键字（`public`、`protected` 或 `private`），与 [`#define`](define.md) 一致。

---

## 说明

`#undef` 通常与条件配合使用，以重新定义某个值或清除功能标志：

```ini
#define EnableLogging
; … 之后 …
#undef EnableLogging      ; 此后功能标志消失

#ifdef EnableLogging
  ; 不再输出
#endif
```

对从未定义的名称使用 `#undef` 没有任何效果。`#undef` 与测试名称当前是否已定义的
[条件指令](conditionals.md)配合时最为有用。

---

## 编辑器支持

- 指令关键字（以及可选的作用域关键字）会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证。
- `#undef Name` 中的名称会**解析**到其 `#define` —— 跳转到定义（**Ctrl+B** / **Cmd+B**）、查找用法
  （**Alt+F7**）和重命名会同步 `#define`、`#undef` 及所有 `{#Name}` 用法。
- `#undef ` 之后的补全会提供作用域关键字以及先前定义的宏名称。
- 没有匹配 `#define` 的 `#undef` 不起任何作用：其名称会变灰，并提供删除该指令的快速修复。

---

参阅官方 [`#define` / `#undef` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_define.htm)。
