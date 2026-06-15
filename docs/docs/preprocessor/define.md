# `#define`

`#define` declares a preprocessor macro — a named value or expression that is substituted into the script
at compile time. It is the most-used ISPP directive and the one the plugin supports with full semantics
(reference resolution, rename and Find Usages).

---

## Syntax

```ini
#define Name [Value]
#define Name(Param1, Param2) Expression
```

- `#define Name Value` defines a constant macro (the value may be omitted, defining a *void* macro).
- `#define Name(params) Expression` defines a function-like macro. The plugin flags a function-like macro
  that has no expression body as an error.
- `#undef Name` removes a previously defined macro.

---

## Using a macro: `{#Name}`

Inside a normal script line, `{#Name}` (short for `{#emit Name}`) emits the macro's value:

```ini
#define MyAppVersion "1.5.0"

[Setup]
AppVersion={#MyAppVersion}
OutputBaseFilename=setup-{#MyAppVersion}
```

- `{#Name}` resolves to its `#define` declaration — go-to-definition (**Ctrl+B** / **Cmd+B**) and
  Find Usages (**Alt+F7**) work, and rename keeps the declaration and all usages in sync.
- It is offered in completion both right after `{` and after `{#`.
- A `#define` that is never used is flagged with a quick-fix to remove it.

---

## Standard predefined variables

Besides your own `#define`s, ISPP ships a set of **standard predefined variables** available without
declaring them. The **value-bearing** ones are emitted inline with `{#…}` exactly like a user define:

| Variable | Meaning |
|----------|---------|
| `{#SourcePath}` | Directory of the root script file |
| `{#CompilerPath}` | Directory of the Inno Setup compiler (`ISCC.exe`) |
| `{#SysPath}` | System directory |
| `{#__FILENAME__}`, `{#__PATHFILENAME__}`, `{#__DIR__}`, `{#__INCLUDE__}` | Current file / path components |
| `{#__LINE__}`, `{#__COUNTER__}` | Current line number / auto-incrementing counter |
| `{#Ver}`, `{#PREPROCVER}` | Preprocessor version |
| `{#NewLine}`, `{#Tab}` | Literal control characters |

These appear in `{#…}` completion and are accepted by validation. The path-relevant ones
(`{#SourcePath}`, `{#__DIR__}`, `{#CompilerPath}`, `{#SysPath}`) are also expanded when the plugin resolves
a `[Languages]` `MessagesFile` path; the remaining, dynamic ones are left unresolved rather than producing
a false error.

!!! note "Valueless symbols"
    `__WIN32__`, `ISPP_INVOKED`, `ISCC_INVOKED`, `WINDOWS` and `UNICODE` carry **no value**: they are only
    *defined* for conditional compilation (`#ifdef` / `#if defined(...)`) and therefore **cannot** be emitted
    via `{#…}`. They are excluded from `{#…}` completion and are not accepted as inline emissions.
