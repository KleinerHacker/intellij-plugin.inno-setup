# `#if` / `#elif` / `#else` / `#endif` and `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`

Conditional directives include or exclude parts of the script at compile time. Everything between an
opening directive and its matching `#endif` is only emitted when the condition holds.

---

## Syntax

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

## Description

- **`#if` / `#elif` / `#else` / `#endif`** form an if/else-if/else chain driven by integer expressions
  (non-zero means true). `#elif` and `#else` are optional; every block must be closed by `#endif`.
- **`#ifdef` / `#ifndef`** test whether a macro is (not) defined — a shorthand for `#if defined(Name)` and
  `#if !defined(Name)`.
- **`#ifexist` / `#ifnexist`** test whether a file does (not) exist on disk.

```ini
#define Beta

#ifdef Beta
  #define AppSuffix " (Beta)"
#else
  #define AppSuffix ""
#endif

#if VER >= 0x06000000
  ; only for newer Inno Setup versions
#endif

#ifexist "extra\\readme.txt"
  #include "extra\\readme.txt"
#endif
```

---

## Editor support

All conditional keywords are highlighted, completed (after `#`) and validated against the bundled ISPP
specification; the controlling expression is parsed and type-checked.

---

See the official [`#if` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm).
