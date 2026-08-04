# `#define`

`#define` 声明一个预处理器宏——一个在编译时被替换进脚本的命名值或表达式。它是最常用的 ISPP
指令，也是插件提供完整语义支持（引用解析、重命名和查找用法）的指令。

---

## 语法

```ini
#define [Scope] Name [Value]
#define [Scope] Name(Param1, Param2) Expression
```

- `#define Name Value` 定义一个常量宏（可省略值，从而定义一个 *void* 宏）。
- `#define Name(params) Expression` 定义类函数宏。插件会将没有表达式主体的类函数宏标记为错误。
- 名称前可加可选的**作用域关键字**（`public`、`protected` 或 `private`），
  例如 `#define public MyAppVersion "1.5.0"`。它会作为关键字被高亮和补全；无论是否使用，名称解析与引用
  的行为都相同。
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

## `#define` 中的表达式与运算

`#define` 的值不仅仅是字面量——它是一个完整的**表达式**。ISPP 在编译时使用类 C/C++
的表达式语法对其求值，插件会对其进行解析、类型检查和高亮。单个字面量是最简单的情形；一旦组合多个值，就必须用**运算符**连接它们。

```ini
#define Major     1
#define Minor     5
#define Build     100
#define Version   Str(Major) + "." + Str(Minor)   ; 字符串连接
#define NextBuild (Build + 1)                      ; 算术，带括号分组
#define OutputDir "Builds\\" + Version             ; 与另一个宏连接
#define IsBeta    Build < 200                       ; 比较 → 整数 0/1
```

### 类型系统

每个表达式都具有以下类型之一；插件会推断它以验证运算：

| 类型     | 来源                           | 说明                                 |
|--------|------------------------------|------------------------------------|
| `int`  | 整数字面量（`100`）、算术/比较/逻辑结果      |                                    |
| `str`  | 字符串字面量（`"x"`、`'x'`）、返回字符串的函数 | 单引号或双引号；重复的引号 `""` 表示字面量引号         |
| `void` | **没有**值的 `#define`           | 与 `int`（作为 `0`）和 `str`（作为 `""`）都兼容 |
| `any`  | 未解析的引用、宏参数、未知函数结果、`{…}` 常量   | 抑制类型检查——绝不产生误报                     |

### 运算符

ISPP 支持完整的类 C/C++ 运算符集。插件会高亮每个运算符标记并应用以下类型规则。

| 分组     | 运算符                         | 操作数类型                       | 结果                |
|--------|-----------------------------|-----------------------------|-------------------|
| 算术     | `+` `-` `*` `/` `%`         | `int`（`+` 也可 `str` + `str`） | `int`（连接时为 `str`） |
| 字符串连接  | `+`                         | `str` + `str`               | `str`             |
| 比较     | `<` `>` `<=` `>=` `==` `!=` | 两者都是 `int` **或**都是 `str`    | `int`（`0`/`1`）    |
| 逻辑     | `&&` `\|\|` `!`             | `int`                       | `int`             |
| 位运算    | `&` `\|` `^` `~`            | `int`                       | `int`             |
| 移位     | `<<` `>>`                   | `int`                       | `int`             |
| 三元     | `cond ? a : b`              | 条件为 `int`                   | 分支的类型             |
| 一元（前缀） | `+` `-` `~` `!`             | `int`                       | `int`             |
| 分组     | `( … )`                     | —                           | 内部表达式的类型          |
| 逗号     | `a , b`                     | —                           | 右操作数的类型           |

**优先级**（越高结合越紧，遵循 C/C++）：
`( )` 和函数调用 → 一元 `+ - ~ !` → `* / %` → `+ -` → `<< >>` → `< > <= >=` → `== !=` →
`&` → `^` → `|` → `&&` → `||` → `?:` → `,`。
拿不准时请使用括号——`#define X 1 + 2 * 3` 为 `7`，`#define X (1 + 2) * 3` 为 `9`。

### 函数调用提供类型

`#define` 的值可以调用任何 ISPP **内置函数**（完整的官方集合已随插件打包），函数的返回类型会流入周围的表达式——例如
`Str(...)` 为 `str`，`Int(...)` 和 `Power(...)` 为 `int`，`FileExists(...)` 为 `int`。因此 `Str(Major) + "."` 是有效的（
`str` + `str`），而 `Str(Major) * 2` 会被拒绝。

```ini
#define FullVer  GetFileVersionString("app.exe")      ; str
#define Padded   "v" + Str(Build)                       ; str + str → str
#define Doubled  Power(2, 10) * 2                        ; int * int → int
```

### 内置函数调用会按签名检查

每个内置函数调用都会按照随插件打包的签名（见下方参考）进行校验：

| 示例                                 | 原因                                          |
|--------------------------------------|-----------------------------------------------|
| `#define X Copy("abc")`              | `Copy` 需要 3 个参数                           |
| `#define X Copy("abc", "x", 2)`      | `Index` 声明为 `int`，却传入了字符串             |
| `#define X StringChange("lit", …)`   | 按引用传递的参数（`S: str*`）需要宏名而非字面量   |
| `#define X Warning("x") + 1`         | `Warning` 不返回值，不能用作表达式的值           |
| `#define X NoSuchFunc(1)`            | 未知的预处理器函数                              |

带默认值的参数（例如 `Find(S, Substr, Index = 1)`）可以省略，只要所有必需参数都已传入，参数较少的调用也会被接受。

标注为 `Ident` 或 `Array` 的参数（`Defined(Ident)`、`TypeOf(Ident)`、`DimOf(Array)`）接收的是符号的**名称**而非值：
参数必须是纯标识符。对 `Defined`/`TypeOf` 而言可以尚未定义；对 `DimOf` 而言数组必须存在，但不带索引传入。

```ini
#dim Langs[2]
#define HasDebug  Defined(DEBUG)   ; DEBUG 可以不存在
#define Count     DimOf(Langs)     ; 是数组本身，而不是 Langs[0]
```

### 输入调用时的参数信息

将光标置于参数列表内并按 **Ctrl+P**（参数信息），会显示被调用函数的参数列表，并高亮你正在输入的那个参数：

* **内置函数**直接显示其签名——`S: str, Index: int, Count: int → str`，包含表示按引用传递的 `*` 以及可选参数的默认值；
* **函数式宏**若声明了类型、`*` 或默认值，则直接显示该声明
  （`#define Multiply(int A, int B = 10)` → `A: int, B: int = 10 → int`）；未声明的参数显示从宏体推断出的类型
  （`#define Pad(n) "0" + Str(n)` → `n: int → str`），无法确定时则只显示参数名。

### 递归引用解析

对另一个宏的引用会取**该宏的**类型，并通过名称递归解析——因此即使操作数本身是 `#define`，也能捕获类型错误：

```ini
#define A "x"
#define B 5
#define C A * B     ; A 是 str，B 是 int → "str * int" 会在 * 上被标记
```

解析只跟随**向后**引用（宏必须已在前面的行声明）。这使得格式正确的脚本不可能形成引用环。残留的环（例如自引用
`#define P P + 1`，或顺序错乱的相互引用）会被安全地打断：引用退化为 `any`，因此既不会无限循环也不会产生误报。

### 插件标记为错误的内容

每个问题都报告为**错误**，并锚定到确切的出错标记（而非整行）：

| 示例                    | 标记的标记 | 原因              |
|-----------------------|-------|-----------------|
| `#define X "a" * "b"` | `*`   | 对字符串操作数使用算术运算符  |
| `#define X 1 + "s"`   | `+`   | 在 `+` 中混用整数和字符串 |
| `#define X "a" < 1`   | `<`   | 比较字符串与整数        |
| `#define X -"s"`      | `"s"` | 对字符串操作数使用一元负号   |
| `#define X 5 6`       | `6`   | 两个操作数之间缺少运算符    |
| `#define X (1 + 2`    | `(`   | 括号不匹配           |

涉及 `any` 操作数（未解析的引用、宏参数、未知函数或 `{…}` 常量）的表达式会被有意地**不**标记，以避免在有效脚本上产生误报。

### 函数式宏体

表达式规则同样适用于函数式宏体。未声明类型的参数默认为 `any`，除非宏体对其加以约束——`x * 2` 只能用于整数，
因此 `x` 被视为 `int`：

```ini
#define Max(a, b) a > b ? a : b
#define Clamp(x)  x < 0 ? 0 : x
```

在宏体内参数处于作用域中：会出现在代码补全里，**Ctrl+B** 可从使用处跳转到参数列表中的声明，查找用法可列出所有使用处，
**Shift+F6** 只在该宏内重命名参数——绝不会改动宏名。

### 宏参数：类型、按引用传递与默认值

ISPP 将参数声明为 `[<类型>] [*]<名称> [= <默认值>]`，其中 `<类型>` 为 `any`、`int`、`str` 或 `func`。
插件会解析该声明，并据此校验每一次调用：

```ini
#define Multiply(int A, int B = 10)  A * B
#define Pad(str S, int Width = 2)    S + Copy("00", 1, Width)
#define Split(str S, str *Rest)      Copy(S, 1, Pos(",", S) - 1)
```

| 声明               | 含义                                                     |
|--------------------|----------------------------------------------------------|
| `int A`            | 实参必须与整数兼容，否则将被标记                         |
| `str S`            | 实参必须与字符串兼容                                     |
| `any V` / 无类型   | 接受任何实参（无类型时宏体仍可能将其收窄）               |
| `func F`           | 以宏为值的参数——按 `any` 处理                             |
| `B = 10`           | 可选：实参可以省略，取值时使用默认值                     |
| `*Rest`            | 按引用传递：实参必须是 ISPP 能回写的宏名                 |

因此调用的校验与内置函数完全一致：实参个数必须介于必需参数个数与已声明参数个数之间，且每个实参都要符合对应参数的类型。

```ini
#define X  Multiply(2)          ; 正常——B 取默认值 10
#define Y  Multiply()           ; 错误：需要 1 到 2 个实参，实际为 0
#define Z  Multiply("a", 2)     ; 错误：实参 1（'A'）必须为 int，实际为 str
#define W  Split("a,b", "lit")  ; 错误：实参 2（'Rest'）按引用传递
```

已声明的类型、`*` 和默认值同样会显示在参数信息弹窗（**Ctrl+P**）和宏的快速文档（**Ctrl+Q**）中。

---

## 内置函数参考

ISPP 提供大量可在 `#define` 表达式中调用的**内置函数**。插件捆绑了完整的官方集合；每个函数的返回类型会供给表达式类型检查器（见上文），并在补全中提供。下表是详尽的，并（与官方
ISPP 函数索引一致）按字母顺序排列。

!!! note "记法"
标记为 `Name: int*` / `Name: str*` 的参数按**引用传递**——函数会写回所提供的变量。末尾的 `= value` 表示带默认值的**可选**
参数。

| 函数                                                                                                 | 返回     | 说明                                                                                                |
|----------------------------------------------------------------------------------------------------|--------|---------------------------------------------------------------------------------------------------|
| `AddBackslash(S: str): str`                                                                        | `str`  | 如果 S 末尾没有反斜杠则添加。                                                                                  |
| `AddQuotes(S: str): str`                                                                           | `str`  | 如果 S 包含空格，则用双引号将其括起。                                                                              |
| `ChangeFileExt(Filename: str, Extension: str): str`                                                | `str`  | 返回将 Filename 的扩展名替换为 Extension 后的结果。                                                              |
| `ComparePackedVersion(Version1: int, Version2: int): int`                                          | `int`  | 比较两个打包（编码）的版本号；返回 -1、0 或 1。                                                                       |
| `Copy(S: str, Index: int, Count: int): str`                                                        | `str`  | 返回 S 的子字符串。Index 从 1 开始。                                                                          |
| `CopyFile(ExistingFile: str, NewFile: str): int`                                                   | `int`  | 在编译时复制现有文件；成功时返回非零值。                                                                              |
| `DecodeVer(Version: int): str`                                                                     | `str`  | 将打包的版本号转换为以点分隔的版本字符串。                                                                             |
| `Defined(Ident): int`                                                                              | `int`  | 如果标识符已定义则返回 1，否则返回 0。                                                                             |
| `Delete(S: str*, Index: int, Count: int)`                                                          | `void` | 从 Index 开始删除 S 中的 Count 个字符（按引用修改 S）。                                                             |
| `DeleteFile(Filename: str): int`                                                                   | `int`  | 在编译时删除文件；成功时返回非零值。                                                                                |
| `DeleteFileNow(Filename: str): int`                                                                | `int`  | 在预处理期间立即删除文件；成功时返回非零值。                                                                            |
| `DimOf(Array): int`                                                                                | `int`  | 返回数组变量的元素个数。                                                                                      |
| `DirExists(Path: str): int`                                                                        | `int`  | 如果目录存在则返回 1，否则返回 0。                                                                               |
| `EmitLanguagesSection()`                                                                           | `void` | 从捆绑的语言文件生成 [Languages] 节。                                                                         |
| `EncodeVer(Major: int, Minor: int, Revision: int = 0, Build: int = 0): int`                        | `int`  | 将版本组成部分编码为单个打包的版本号。                                                                               |
| `EntryCount(Section: str): int`                                                                    | `int`  | 返回指定脚本节中的条目数。                                                                                     |
| `Error(Message: str)`                                                                              | `void` | 使用给定消息引发编译时错误。                                                                                    |
| `Exec(CmdLine: str, Params: str = "", WorkingDir: str = "", ShowCmd: int = 0, Wait: int = 0): int` | `int`  | 在编译时执行程序；返回进程退出代码。                                                                                |
| `ExecAndGetFirstLine(CmdLine: str, Params: str = "", WorkingDir: str = ""): str`                   | `str`  | 执行程序并返回其标准输出的第一行。                                                                                 |
| `ExtractFileDir(Filename: str): str`                                                               | `str`  | 返回 Filename 的目录部分（不含末尾反斜杠）。                                                                       |
| `ExtractFileExt(Filename: str): str`                                                               | `str`  | 返回 Filename 的扩展名（含前导点）。                                                                           |
| `ExtractFileName(Filename: str): str`                                                              | `str`  | 返回 Filename 的名称和扩展名部分。                                                                            |
| `ExtractFilePath(Filename: str): str`                                                              | `str`  | 返回 Filename 的驱动器和目录部分（含末尾反斜杠）。                                                                    |
| `FileClose(Handle: int)`                                                                           | `void` | 关闭先前用 FileOpen 打开的文件。                                                                             |
| `FileEof(Handle: int): int`                                                                        | `int`  | 当到达已打开文件的末尾时返回非零值。                                                                                |
| `FileExists(Filename: str): int`                                                                   | `int`  | 如果文件存在则返回 1，否则返回 0。                                                                               |
| `FileOpen(Filename: str): int`                                                                     | `int`  | 以只读方式打开文本文件并返回文件句柄。                                                                               |
| `FileRead(Handle: int): str`                                                                       | `str`  | 从已打开的文件中读取下一行。                                                                                    |
| `FileReset(Handle: int)`                                                                           | `void` | 将已打开文件的读取位置重置到开头。                                                                                 |
| `FileSize(Filename: str): int`                                                                     | `int`  | 返回文件的大小（字节）。                                                                                      |
| `Find(S: str, Substr: str, Index: int = 1): int`                                                   | `int`  | 返回从 Index 开始 S 中 Substr 的位置，找不到则返回 0。                                                             |
| `FindClose(Handle: int)`                                                                           | `void` | 关闭用 FindFirst 打开的搜索句柄。                                                                            |
| `FindCode(): int`                                                                                  | `int`  | 返回 [Code] 节开始的行索引。                                                                                |
| `FindFirst(Pattern: str, Attributes: int = 0): int`                                                | `int`  | 开始文件搜索并返回句柄；若无匹配则返回负值。                                                                            |
| `FindGetFileName(Handle: int): str`                                                                | `str`  | 返回当前 FindFirst/FindNext 找到的文件名。                                                                   |
| `FindNext(Handle: int): int`                                                                       | `int`  | 将文件搜索推进到下一个匹配项；成功时返回非零值。                                                                          |
| `FindSection(Section: str): int`                                                                   | `int`  | 返回给定节标题的行索引。                                                                                      |
| `FindSectionEnd(Section: str): int`                                                                | `int`  | 返回给定节最后一个条目之后的行索引。                                                                                |
| `ForceDirectories(Dir: str): int`                                                                  | `int`  | 在编译时创建目录树；成功时返回非零值。                                                                               |
| `GetDateTimeString(Format: str, DateSep: str, TimeSep: str): str`                                  | `str`  | 按 Format 格式化并返回当前日期/时间。                                                                           |
| `GetEnv(Name: str): str`                                                                           | `str`  | 返回环境变量的值。                                                                                         |
| `GetFileCompanyString(Filename: str): str`                                                         | `str`  | 从文件的版本信息中返回 CompanyName 字符串。                                                                      |
| `GetFileCopyrightString(Filename: str): str`                                                       | `str`  | 从文件的版本信息中返回 LegalCopyright 字符串。                                                                   |
| `GetFileDateTimeString(Filename: str, Format: str, DateSep: str, TimeSep: str): str`               | `str`  | 按 Format 格式化并返回文件的最后修改时间戳。                                                                        |
| `GetFileDescriptionString(Filename: str): str`                                                     | `str`  | 从文件的版本信息中返回 FileDescription 字符串。                                                                  |
| `GetFileOriginalFilenameString(Filename: str): str`                                                | `str`  | 从文件的版本信息中返回 OriginalFilename 字符串。                                                                 |
| `GetFileProductVersionString(Filename: str): str`                                                  | `str`  | 从文件的版本信息中返回 ProductVersion 字符串。                                                                   |
| `GetFileVersionString(Filename: str): str`                                                         | `str`  | 以点分隔的字符串返回可执行文件或 DLL 的文件版本，例如 <code>1.2.3.4</code>。                                               |
| `GetMD5OfFile(Filename: str): str`                                                                 | `str`  | 以十六进制字符串返回文件的 MD5 哈希。                                                                             |
| `GetMD5OfString(S: str): str`                                                                      | `str`  | 以十六进制字符串返回 ANSI 字符串的 MD5 哈希。                                                                      |
| `GetMD5OfUnicodeString(S: str): str`                                                               | `str`  | 以十六进制字符串返回 Unicode 字符串的 MD5 哈希。                                                                   |
| `GetPackedVersion(Filename: str): int`                                                             | `int`  | 返回文件的打包（编码）版本号。                                                                                   |
| `GetSHA1OfFile(Filename: str): str`                                                                | `str`  | 以十六进制字符串返回文件的 SHA-1 哈希。                                                                           |
| `GetSHA1OfString(S: str): str`                                                                     | `str`  | 以十六进制字符串返回 ANSI 字符串的 SHA-1 哈希。                                                                    |
| `GetSHA1OfUnicodeString(S: str): str`                                                              | `str`  | 以十六进制字符串返回 Unicode 字符串的 SHA-1 哈希。                                                                 |
| `GetSHA256OfFile(Filename: str): str`                                                              | `str`  | 以十六进制字符串返回文件的 SHA-256 哈希。                                                                         |
| `GetSHA256OfString(S: str): str`                                                                   | `str`  | 以十六进制字符串返回 ANSI 字符串的 SHA-256 哈希。                                                                  |
| `GetSHA256OfUnicodeString(S: str): str`                                                            | `str`  | 以十六进制字符串返回 Unicode 字符串的 SHA-256 哈希。                                                               |
| `GetStringFileInfo(Filename: str, Key: str): str`                                                  | `str`  | 从文件的版本信息中返回字符串。常用键：<code>FileVersion</code>、<code>ProductVersion</code>、<code>CompanyName</code>。 |
| `GetVersionComponents(Filename: str, Major: int*, Minor: int*, Revision: int*, Build: int*): int`  | `int`  | 将文件的版本组成部分读入引用变量；成功时返回非零值。                                                                        |
| `GetVersionNumbers(Filename: str, VersionMS: int*, VersionLS: int*): int`                          | `int`  | 将文件的版本读入引用的高/低位字；成功时返回非零值。                                                                        |
| `GetVersionNumbersString(Filename: str): str`                                                      | `str`  | 以点分隔的字符串返回文件的版本，例如 <code>1.2.3.4</code>。                                                          |
| `Insert(Source: str, S: str*, Index: int)`                                                         | `void` | 将 Source 插入到 S 的 Index 处（按引用修改 S）。                                                                |
| `Int(Value: any, Default: int = 0): int`                                                           | `int`  | 将值转换为整数，转换失败时使用 Default。                                                                          |
| `Is64BitPEImage(Filename: str): int`                                                               | `int`  | 如果给定的 PE 映像是 64 位则返回非零值。                                                                          |
| `IsWin64(): int`                                                                                   | `int`  | 当编译器运行在 64 位 Windows 上时返回非零值。                                                                     |
| `Len(S: str): int`                                                                                 | `int`  | 返回字符串的长度。                                                                                         |
| `LowerCase(S: str): str`                                                                           | `str`  | 返回转换为小写的字符串。                                                                                      |
| `Max(A: int, B: int): int`                                                                         | `int`  | 返回两个整数中较大的一个。                                                                                     |
| `Message(S: str)`                                                                                  | `void` | 在编译器日志中输出一条信息性消息。                                                                                 |
| `Min(A: int, B: int): int`                                                                         | `int`  | 返回两个整数中较小的一个。                                                                                     |
| `PackVersionComponents(Major: int, Minor: int, Revision: int, Build: int): int`                    | `int`  | 将版本组成部分打包为单个打包的版本号。                                                                               |
| `PackVersionNumbers(VersionMS: int, VersionLS: int): int`                                          | `int`  | 将高/低位版本字打包为单个打包的版本号。                                                                              |
| `Pos(Substr: str, S: str): int`                                                                    | `int`  | 返回 S 中 Substr 从 1 开始的位置，找不到则返回 0。                                                                 |
| `Power(Base: int, Exponent: int): int`                                                             | `int`  | 返回 Base 的 Exponent 次幂。                                                                            |
| `ReadIni(Filename: str, Section: str, Key: str, Default: str = ""): str`                           | `str`  | 在编译时从 INI 文件读取值。                                                                                  |
| `ReadReg(RootKey: int, SubKeyName: str, ValueName: str = "", Default: str = ""): str`              | `str`  | 在编译时读取注册表值。                                                                                       |
| `RemoveBackslashUnlessRoot(S: str): str`                                                           | `str`  | 除非 S 是驱动器根目录，否则删除 S 末尾的反斜杠。                                                                       |
| `RemoveFileExt(Filename: str): str`                                                                | `str`  | 返回去掉扩展名后的 Filename。                                                                               |
| `RPos(Substr: str, S: str): int`                                                                   | `int`  | 返回 S 中 Substr 最后一次出现的从 1 开始的位置，找不到则返回 0。                                                          |
| `SamePackedVersion(Version1: int, Version2: int): int`                                             | `int`  | 如果两个打包的版本号相等则返回非零值。                                                                               |
| `SameStr(S1: str, S2: str): int`                                                                   | `int`  | 如果两个字符串相等（不区分大小写）则返回非零值。                                                                          |
| `SameText(S1: str, S2: str): int`                                                                  | `int`  | 如果两个字符串相等（不区分大小写）则返回非零值。                                                                          |
| `SaveStringToFile(Filename: str, S: str, Append: int = 0)`                                         | `void` | 将字符串写入文件，可选择追加。                                                                                   |
| `SaveToFile(Filename: str)`                                                                        | `void` | 将到目前为止收集的预处理输出写入文件（调试辅助）。                                                                         |
| `SetSetupSetting(Name: str, Value: str)`                                                           | `void` | 在编译时设置 [Setup] 节指令。                                                                               |
| `SetupSetting(Name: str): str`                                                                     | `str`  | 按名称返回 [Setup] 节指令的值。                                                                              |
| `Str(Value: any): str`                                                                             | `str`  | 将值转换为字符串。整数变为文本，void 变为空字符串。                                                                      |
| `StringChange(S: str*, FromStr: str, ToStr: str): int`                                             | `int`  | 将 S 中所有的 FromStr 替换为 ToStr；返回替换次数。                                                                |
| `StrToVersion(S: str): int`                                                                        | `int`  | 将以点分隔的版本字符串解析为打包的版本号。                                                                             |
| `Trim(S: str): str`                                                                                | `str`  | 返回去掉首尾空白后的 S。                                                                                     |
| `TypeOf(Ident): int`                                                                               | `int`  | 返回标识符的类型：0=void、1=int、2=str。                                                                      |
| `UnpackVersionComponents(Version: int, Major: int*, Minor: int*, Revision: int*, Build: int*)`     | `void` | 将打包的版本号拆分到引用的组成部分中。                                                                               |
| `UnpackVersionNumbers(Version: int, VersionMS: int*, VersionLS: int*)`                             | `void` | 将打包的版本号拆分到引用的高/低位字中。                                                                              |
| `UpperCase(S: str): str`                                                                           | `str`  | 返回转换为大写的字符串。                                                                                      |
| `VersionToStr(Version: int): str`                                                                  | `str`  | 将打包的版本号转换为以点分隔的版本字符串。                                                                             |
| `Warning(Message: str)`                                                                            | `void` | 使用给定消息发出编译时警告。                                                                                    |
| `WriteIni(Filename: str, Section: str, Key: str, Value: str)`                                      | `void` | 在编译时将值写入 INI 文件。                                                                                  |
| `YesNo(S: str): int`                                                                               | `int`  | 如果字符串表示肯定（yes/true）值则返回非零值。                                                                       |

---

## 预定义变量

除了你自己的 `#define`，ISPP 还提供一组无需声明即可使用的**预定义变量**。其中**带值的**变量（`int` / `str`）可以像用户定义一样通过
`{#…}` 内联输出，也可在表达式中使用；**无值的**变量（`void`）仅用于条件编译。下表是完整的：

| 变量                 | 类型     | 说明                              |
|--------------------|--------|---------------------------------|
| `__COUNTER__`      | `int`  | 自动递增计数器；每次使用时递增。                |
| `__LINE__`         | `int`  | 当前文件中的当前行号。                     |
| `__FILENAME__`     | `str`  | 当前包含文件路径的文件名部分。                 |
| `__PATHFILENAME__` | `str`  | 当前包含文件的完整路径。                    |
| `__DIR__`          | `str`  | 当前包含文件路径的目录部分。                  |
| `__INCLUDE__`      | `str`  | 当前包含路径（多个路径以分号分隔）。              |
| `__WIN32__`        | `void` | 始终已定义。可与 #ifdef 一起用于检测 ISPP 环境。 |
| `ISPP_INVOKED`     | `void` | 当 ISPP 处于活动状态时始终已定义。            |
| `ISCC_INVOKED`     | `void` | 当使用控制台模式编译器（ISCC.exe）编译时已定义。    |
| `PREPROCVER`       | `int`  | Inno Setup 预处理器的 32 位打包版本号。     |
| `Ver`              | `int`  | PREPROCVER 的别名。                 |
| `WINDOWS`          | `void` | 始终已定义。                          |
| `UNICODE`          | `void` | 始终已定义（ISPP 仅支持 Unicode）。        |
| `CompilerPath`     | `str`  | Inno Setup 编译器（ISCC.exe）所在的目录。  |
| `SourcePath`       | `str`  | 包含根脚本文件的目录。                     |
| `SysPath`          | `str`  | 与编译器类型相对应的系统目录。                 |
| `NewLine`          | `str`  | 换行符序列。                          |
| `Tab`              | `str`  | 制表符。                            |

这些变量出现在 `{#…}` 补全中并被验证接受。与路径相关的变量（`{#SourcePath}`、`{#__DIR__}`、`{#CompilerPath}`、`{#SysPath}`
）在插件解析 `[Languages]` 的 `MessagesFile` 路径时也会被展开；其余动态变量保持未解析，而不会产生误报错误。

!!! note "无值符号"
`__WIN32__`、`ISPP_INVOKED`、`ISCC_INVOKED`、`WINDOWS` 和 `UNICODE` **没有值**：它们仅为条件编译（`#ifdef` /
`#if defined(...)`）而*定义*，因此**不能**通过 `{#…}` 输出。它们被排除在 `{#…}` 补全之外，也不被接受为内联输出。
