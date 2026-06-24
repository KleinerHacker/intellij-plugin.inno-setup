# `#dim` / `#redim`

`#dim` declares an **array** macro, and `#redim` changes the size of an existing array while keeping its
contents. Arrays let you store an indexed list of values that you can iterate over with [`#for`](for.md).

---

## Syntax

```ini
#dim [private | protected | public] Name[Size] [{ Init, Init, ... }]
#redim [private | protected | public] Name[NewSize]
```

- `Size` / `NewSize` are integer expressions (literals, other `#define`s, `DimOf(...)`, …).
- `#dim` creates the array `Name` with that many elements; every element starts out **void** (empty).
- The optional `{ ... }` list initialises the leading elements in order (`{1, 2, 3}` fills indices `0`–`2`).
- `#redim` resizes a previously declared array; existing elements within the new bounds are preserved and any
  newly added elements are void.
- The optional scope keyword (`private` / `protected` / `public`) controls visibility, exactly as for
  [`#define`](define.md).

---

## Description

Arrays are **zero-based**: an array of size `N` has valid indices `0 … N-1`. Elements are addressed with
`Name[Index]` and assigned with [`#define`](define.md). A typical pattern fills an array and then walks it:

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < DimOf(Servers); i++} \
  #pragma message Servers[i]
```

The same can be written more compactly with an inline initialiser:

```ini
#dim Servers[3] {"alpha", "beta", "gamma"}
```

Element values participate in expressions like any other value — they can be read, combined and even reference
other macros:

```ini
#define Base 10
#dim Offsets[2] {Base, Base + 5}
#if Offsets[1] > Offsets[0]
  ; ...
#endif
```

Use `#redim` when the number of elements is only known later, for example after counting items:

```ini
#redim Servers[5]   ; grow the array, keeping the first three values
```

`DimOf(Name)` returns the current element count of an array.

---

## Editor support

- The `#dim` / `#redim` keywords, scope keywords and array names are highlighted and validated against the
  bundled ISPP specification; `[` and `]` are matched as a brace pair.
- Array names are offered in completion: after `#redim ` (existing arrays) and inside any expression
  (declared arrays, shown with a trailing `[]`).
- `Name[Index]`, `#redim Name`, `#define Name[Index]` and `DimOf(Name)` all navigate (Ctrl/Cmd-click) and
  rename together with their originating `#dim`.
- Element values can be statically evaluated across `#dim`/`#define` (e.g. for documentation popups).

The following mistakes are reported as errors:

- indexing a name that is **not** an array (`Foo[0]` where `Foo` is a plain `#define`);
- using an array name **without** an index in an expression (`Servers` instead of `Servers[i]`);
- a **non-integer** array index or size;
- `#redim` of an array that was never `#dim`-declared;
- an inline initialiser whose element count does not match the declared size;
- a statically **out-of-bounds** index (`#define Servers[9]` or `Servers[9]` on an array of size 3).

Dynamic indices (e.g. a loop variable) are intentionally **not** flagged, to avoid false positives.

---

See the official [`#dim` / `#redim` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm).
