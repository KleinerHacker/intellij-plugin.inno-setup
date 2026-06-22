# `#sub` / `#endsub`

`#sub` 开始一个**子例程** —— 可重复调用的命名指令块 —— 而 `#endsub` 结束它。子例程最常用作
[`#for`](for.md) 循环执行的主体。

---

## 语法

```ini
#sub Name
  ; 指令 …
#endsub
```

---

## 说明

`#sub Name` 与 `#endsub` 之间的所有内容都以 `Name` 存储，并在每次调用子例程时执行（例如通过 `#for` 的
`Func` 参数）。在子例程内部可使用当前循环变量，因此每次调用都可以输出不同内容：

```ini
#dim Files[2]
#define Files[0] "app.exe"
#define Files[1] "help.chm"

#sub EmitFile
  #emit "Source: """ + Files[i] + """; DestDir: ""{app}"""
#endsub

[Files]
#for {i = 0; i < 2; i++; EmitFile}
```

每个 `#sub` 都必须由匹配的 `#endsub` 关闭。

---

## 编辑器支持

两个指令关键字都会被高亮、补全（`#` 之后）并根据内置 ISPP 规范验证。

---

参阅官方 [`#sub` 文档 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_sub.htm)。
