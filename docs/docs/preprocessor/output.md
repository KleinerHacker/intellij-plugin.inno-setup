# `#emit` / `#expr` / `#insert` / `#append`

These directives evaluate an expression and decide what happens with its result. They are the explicit
counterparts to the inline `{#…}` form.

---

## Syntax

```ini
#emit Expression
#expr Expression
#insert Expression
#append Expression
```

---

## Description

- **`#emit`** evaluates the expression and writes its value into the output as a line of script. The inline
  form `{#expr}` inside a normal line is shorthand for `{#emit expr}`.
- **`#expr`** evaluates the expression for its **side effects** only (for example calling a function or
  assigning to a macro) and discards the result — nothing is emitted.
- **`#insert`** and **`#append`** place emitted output at a chosen position relative to the current output
  line — `#insert` before it, `#append` after — which is useful when generating sections programmatically.

```ini
#define AppExe "MyApp.exe"

[Run]
#emit "Filename: ""{app}\\" + AppExe + """; Flags: nowait"

; evaluate for side effect, emit nothing
#expr Local[0] = GetEnv("BUILD_ID")
```

---

## Editor support

All four directive keywords are highlighted, completed (after `#`) and validated against the bundled ISPP
specification; the expression is parsed and type-checked.

---

See the official [`#emit` /
`#expr` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_emit.htm).
