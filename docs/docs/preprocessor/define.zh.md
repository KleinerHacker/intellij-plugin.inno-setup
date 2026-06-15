# `#define`

`#define` 声明一个预处理器宏——一个在编译时被替换进脚本的命名值或表达式。它是最常用的 ISPP 指令，也是插件提供完整语义支持（引用解析、重命名和查找用法）的指令。

---

## 语法

```ini
#define Name [Value]
#define Name(Param1, Param2) Expression
```

- `#define Name Value` 定义一个常量宏（可省略值，从而定义一个 *void* 宏）。
- `#define Name(params) Expression` 定义类函数宏。插件会将没有表达式主体的类函数宏标记为错误。
- `#undef Name` 移除先前定义的宏。

---

## 使用宏：`{#Name}`

在普通脚本行内，`{#Name}`（`{#emit Name}` 的简写）会输出宏的值：

```ini
#define MyAppVersion "1.5.0"

[Setup]
AppVersion={#MyAppVersion}
OutputBaseFilename=setup-{#MyAppVersion}
```

- `{#Name}` 解析到其 `#define` 声明——转到定义（**Ctrl+B** / **Cmd+B**）和查找用法（**Alt+F7**）均可用，重命名会保持声明与所有用法同步。
- 它在 `{` 之后和 `{#` 之后都会出现在补全中。
- 从未使用的 `#define` 会被标记，并提供移除的快速修复。

---

## 标准预定义变量

除了你自己的 `#define`，ISPP 还提供一组无需声明即可使用的**标准预定义变量**。其中**带值的**变量可以像用户定义一样通过 `{#…}` 内联输出：

| 变量 | 含义 |
|------|------|
| `{#SourcePath}` | 根脚本文件所在目录 |
| `{#CompilerPath}` | Inno Setup 编译器（`ISCC.exe`）所在目录 |
| `{#SysPath}` | 系统目录 |
| `{#__FILENAME__}`、`{#__PATHFILENAME__}`、`{#__DIR__}`、`{#__INCLUDE__}` | 当前文件/路径组成部分 |
| `{#__LINE__}`、`{#__COUNTER__}` | 当前行号 / 自增计数器 |
| `{#Ver}`、`{#PREPROCVER}` | 预处理器版本 |
| `{#NewLine}`、`{#Tab}` | 字面控制字符 |

这些变量出现在 `{#…}` 补全中并被验证接受。与路径相关的变量（`{#SourcePath}`、`{#__DIR__}`、`{#CompilerPath}`、`{#SysPath}`）在插件解析 `[Languages]` 的 `MessagesFile` 路径时也会被展开；其余动态变量保持未解析，而不会产生误报错误。

!!! note "无值符号"
    `__WIN32__`、`ISPP_INVOKED`、`ISCC_INVOKED`、`WINDOWS` 和 `UNICODE` **没有值**：它们仅为条件编译（`#ifdef` / `#if defined(...)`）而*定义*，因此**不能**通过 `{#…}` 输出。它们被排除在 `{#…}` 补全之外，也不被接受为内联输出。
