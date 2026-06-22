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

所有条件关键字都会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证；控制表达式会被解析和类型检查。

---

参阅官方 [`#if` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm)。
