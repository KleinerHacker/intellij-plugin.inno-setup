# `#dim` / `#redim`

`#dim` 声明一个**数组**宏，`#redim` 在保留内容的同时更改现有数组的大小。数组让你存储一个带索引的值列表，
可以用 [`#for`](for.md) 遍历。

---

## 语法

```ini
#dim Name[Size]
#redim Name[NewSize]
```

- `#dim` 以给定的元素数量创建数组 `Name`。
- `#redim` 调整先前声明的数组的大小；新边界内的现有元素会被保留。

---

## 说明

元素通过 `Name[Index]` 寻址，并用 [`#define`](define.md) 赋值。典型模式是先填充数组再遍历它：

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < 3; i++} \
  #pragma message Servers[i]
```

当元素数量只有稍后才能确定时（例如在计数项目之后），使用 `#redim`：

```ini
#redim Servers[5]   ; 扩大数组，保留前三个值
```

---

## 编辑器支持

两个指令关键字都会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证。

---

参阅官方 [`#dim` / `#redim` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm)。
