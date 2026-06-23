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

- **Highlighting & completion** — all conditional keywords are highlighted and completed (after `#`) and
  validated against the bundled ISPP specification.
- **Condition expression** — the `#if` / `#elif` condition is the same full ISPP expression as a
  [`#define`](define.md) value: operators are highlighted, syntax and type errors are reported on the
  offending token, and the expression providers (other `#define`s, predefined variables and built-in
  functions) are offered in completion inside the condition.
- **References** — identifiers in the condition resolve to their `#define` declaration, so
  go-to-definition (**Ctrl+B** / **Cmd+B**), Find Usages (**Alt+F7**) and rename work; an unknown name
  is flagged as an *unresolved reference* error (just like in a `#define`). `defined(Name)` is exempt —
  its argument may legitimately be undefined.
- **`#ifdef` / `#ifndef`** — the name resolves to its `#define` declaration (go-to-definition, Find Usages,
  rename) and `#define` names are offered in completion. Unlike a `#if` condition, an unknown name is
  **not** an error — testing an undefined macro is the whole point of `#ifdef` / `#ifndef`.
- **`#ifexist` / `#ifnexist`** — the filename is a full ISPP **string** expression: operators and type
  errors are validated (the value must be a string) and identifiers resolve to their `#define`. (No
  file-name completion is offered — these directives may test *any* file on disk, not just script files.)
- **Boolean literals** — ISPP has no booleans, so a literal `true` / `false` / `yes` / `no` used directly
  in a condition is painted **yellow** and carries a warning (the word is silently treated as an
  undefined identifier `0`).
- **Structure validation** — every opener (`#if` / `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`) must
  be closed by an `#endif` before the end of the file; an unterminated opener, a stray
  `#elif` / `#else` / `#endif` without an open block, and an `#elif` after `#else` are all flagged as
  errors. A `#if` / `#elif` **without a condition** is an error.
- **Folding** — a complete `#if … #endif` block can be collapsed when it lies entirely within a single
  section *or* entirely outside any section (a block that crosses a section header is not folded).

---

See the official [`#if` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm).
