# `#dim` / `#redim`

`#dim` 声明一个**数组**宏，`#redim` 在保留内容的同时更改现有数组的大小。数组让你存储一个带索引的值列表，
可以用 [`#for`](for.md) 进行遍历。

---

## 语法

```ini
#dim [private | protected | public] Name[Size] [{ Init, Init, ... }]
#redim [private | protected | public] Name[NewSize]
```

- `Size` / `NewSize` 是整数表达式（字面量、其他 `#define`、`DimOf(...)` 等）。
- `#dim` 以该元素数量创建数组 `Name`；每个元素初始为 **void**（空）。
- 可选的 `{ ... }` 列表按顺序初始化前面的元素（`{1, 2, 3}` 填充索引 `0`–`2`）。
- `#redim` 调整先前声明的数组大小；新范围内的现有元素被保留，新增元素为 void。
- 可选的作用域关键字（`private` / `protected` / `public`）控制可见性，与 [`#define`](define.md) 相同。

---

## 说明

数组是**从零开始**的：大小为 `N` 的数组有效索引为 `0 … N-1`。元素用 `Name[Index]` 寻址，并用
[`#define`](define.md) 赋值。典型的模式是填充数组然后遍历：

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < DimOf(Servers); i++} \
  #pragma message Servers[i]
```

使用内联初始化器可以写得更简洁：

```ini
#dim Servers[3] {"alpha", "beta", "gamma"}
```

元素值像其他值一样参与表达式——可以读取、组合，甚至引用其他宏：

```ini
#define Base 10
#dim Offsets[2] {Base, Base + 5}
#if Offsets[1] > Offsets[0]
  ; ...
#endif
```

当元素数量只有稍后才知道时（例如在统计项数之后），使用 `#redim`：

```ini
#redim Servers[5]   ; 扩展数组，保留前三个值
```

`DimOf(Name)` 返回数组的当前元素数量。

---

## 编辑器支持

- `#dim` / `#redim` 关键字、作用域关键字和数组名会被高亮并根据内置的 ISPP 规范进行校验；`[` 和 `]` 作为
  括号对进行匹配。
- 数组名在补全中提供：在 `#redim ` 之后（现有数组）以及任意表达式中（已声明的数组，带尾随 `[]`）。
- `Name[Index]`、`#redim Name`、`#define Name[Index]` 和 `DimOf(Name)` 都会与其来源 `#dim` 一起导航
  （Ctrl/Cmd-单击）和重命名。
- 元素值可以跨 `#dim`/`#define` 进行静态求值（例如用于文档弹窗）。

以下错误会被报告：

- 对**非数组**名称建立索引（`Foo` 是普通 `#define` 时的 `Foo[0]`）；
- 在表达式中**不带**索引使用数组名（用 `Servers` 而非 `Servers[i]`）；
- **非整数**的数组索引或大小；
- 对从未 `#dim` 声明的数组使用 `#redim`；
- 内联初始化器的元素数量与声明的大小不一致；
- 静态**越界**索引（大小为 3 的数组上的 `#define Servers[9]` 或 `Servers[9]`）。

动态索引（例如循环变量）有意**不**标记，以避免误报。

---

参见官方 [`#dim` / `#redim` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm)。
