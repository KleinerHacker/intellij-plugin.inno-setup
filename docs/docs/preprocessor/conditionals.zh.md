# `#if` / `#elif` / `#else` / `#endif` 以及 `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`

条件指令在编译时包含或排除脚本的一部分。开始指令与其匹配的 `#endif` 之间的所有内容仅在条件成立时才会
输出。

---

## 语法

```ini
#if Expression
#elif Expression
#else
#endif

#ifdef Name
#ifndef Name
#ifexist "filename"
#ifnexist "filename"
```

---

## 说明

- **`#if` / `#elif` / `#else` / `#endif`** 构成由整数表达式（非零为真）驱动的 if/else-if/else 链。`#elif`
  和 `#else` 是可选的；每个块都必须由 `#endif` 关闭。
- **`#ifdef` / `#ifndef`** 测试宏是否（未）定义 —— 是 `#if defined(Name)` 和 `#if !defined(Name)` 的
  简写。
- **`#ifexist` / `#ifnexist`** 测试磁盘上文件是否（不）存在。

```ini
#define Beta

#ifdef Beta
  #define AppSuffix " (Beta)"
#else
  #define AppSuffix ""
#endif

#if VER >= 0x06000000
  ; 仅用于较新的 Inno Setup 版本
#endif

#ifexist "extra\\readme.txt"
  #include "extra\\readme.txt"
#endif
```

---

## 编辑器支持

- **高亮与补全** —— 所有条件关键字都会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证。
- **条件表达式** —— `#if` / `#elif` 的条件与 [`#define`](define.md) 的值一样是完整的 ISPP 表达式：运算符
  会被高亮，语法和类型错误会标注在出错的标记上，表达式补全候选（其他 `#define`、预定义变量、内置函数）
  在条件中也会提供。
- **引用** —— 条件中的标识符会解析到其 `#define` 声明，因此跳转到定义（**Ctrl+B** / **Cmd+B**）、查找用法
  （**Alt+F7**）和重命名均可用；未知名称会被标记为*未解析引用*错误（与 `#define` 相同）。`defined(Name)`
  例外，其参数允许未定义。
- **`#ifdef` / `#ifndef`** —— 名称会解析到其 `#define` 声明（跳转到定义、查找用法、重命名），补全中会提供
  `#define` 名称。与 `#if` 条件不同，未知名称**不会**报错——判断未定义的宏正是 `#ifdef` / `#ifndef` 的用途。
- **`#ifexist` / `#ifnexist`** —— 文件名是完整的 ISPP **字符串**表达式：会验证运算符和类型错误（值必须是
  字符串），标识符会解析到其 `#define`。（这些指令可以测试磁盘上的*任意*文件，而不仅是脚本文件，因此不提供
  文件名补全。）
- **布尔字面量** —— ISPP 没有布尔类型，因此在条件中直接使用的 `true` / `false` / `yes` / `no` 会被显示为
  **黄色**并带有警告（该词会被静默地当作未定义标识符 `0`）。
- **结构验证** —— 每个开启指令（`#if` / `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`）都必须在文件结束前
  以 `#endif` 关闭；未关闭的开启指令、没有对应开启块的 `#elif` / `#else` / `#endif`，以及位于 `#else`
  之后的 `#elif`，都会被标记为错误。没有条件的 `#if` / `#elif` 也是错误。
- **折叠** —— 当完整的 `#if … #endif` 块完全位于单个节内或完全位于任何节之外时可以折叠（跨越节标题的块
  不会折叠）。

---

参阅官方 [`#if` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm)。
