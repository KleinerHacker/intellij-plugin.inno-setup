# `#dim` / `#redim`

`#dim` declares an **array** macro, and `#redim` changes the size of an existing array while keeping its
contents. Arrays let you store an indexed list of values that you can iterate over with [`#for`](for.md).

---

## Syntax

```ini
#dim Name[Size]
#redim Name[NewSize]
```

- `#dim` creates the array `Name` with the given number of elements.
- `#redim` resizes a previously declared array; existing elements within the new bounds are preserved.

---

## Description

Elements are addressed with `Name[Index]` and assigned with [`#define`](define.md). A typical pattern fills
an array and then walks it:

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < 3; i++} \
  #pragma message Servers[i]
```

Use `#redim` when the number of elements is only known later, for example after counting items:

```ini
#redim Servers[5]   ; grow the array, keeping the first three values
```

---

## Editor support

Both directive keywords are highlighted, completed (after `#`) and validated against the bundled ISPP
specification.

---

See the official [`#dim` / `#redim` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm).
