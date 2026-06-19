# Inno Setup Preprocessor (ISPP)

The Inno Setup Preprocessor (ISPP) runs **before** the script is compiled and lets you generate or alter
the script with directives, macros and expressions. The plugin injects preprocessor lines as their own
language, so they receive dedicated highlighting, completion, validation, rename and Find Usages support.
This section follows the structure of the official
[Inno Setup Preprocessor documentation](https://jrsoftware.org/ishelp/topic_isppoverview.htm).

---

## What is a preprocessor directive?

A directive is a line that begins with `#`. It is evaluated at compile time and never appears in the final
installer. The plugin validates the keyword against its bundled ISPP specification — an unknown directive
(one not defined in the spec) is flagged as an error (case-insensitive), mirroring the
unknown-section/flag/constant checks.

---

## Supported directives

| Group | Directives |
|-------|------------|
| Definition | `#define`, `#undef`, `#dim`, `#redim` |
| Inclusion | `#include`, `#file` |
| Output | `#emit`, `#expr`, `#insert`, `#append` |
| Conditionals | `#if`, `#elif`, `#else`, `#endif`, `#ifdef`, `#ifndef`, `#ifexist`, `#ifnexist` |
| Loops & macros | `#for`, `#sub`, `#endsub` |
| Misc | `#pragma`, `#error` |

All directive keywords are highlighted, completed (after `#`) and validated against the spec. Directives
that the plugin also supports with full semantics (reference resolution, rename, Find Usages) have their
own page:

- [`#define`](define.md) — declare and use macros, plus the standard predefined variables

---

## Inline emission `{#…}`

Inside a normal script line, `{#expr}` is the inline form of `{#emit expr}`: the preprocessor evaluates the
expression and replaces the placeholder with its value. The expression is typically a macro or a predefined
variable. See [`#define`](define.md) for details and the predefined variables usable this way.

---

## Working with `#include`

An `#include "file"` path resolves to the referenced `.iss` file (relative to the including script's
directory; absolute paths are used as-is). **Ctrl+B** / **Cmd+B** jumps to the file, and the path is
completed as you type. Two editor actions make working with includes easier:

- **Inline `#include` content** — with the caret on an `#include` line that resolves to an existing file,
  the intention (**Alt+Enter**) replaces the line with the verbatim content of that file. Only one level
  is expanded; nested `#include` lines in the inserted text are kept as-is. Afterwards you are asked whether
  the now-inlined include file should be deleted (default: no).
- **Extract selection to `#include` file** — select one or more lines and invoke the intention to move
  them into a new file next to the current script. A file name is requested (and you are asked before an
  existing file is overwritten); the selected full lines are replaced with an `#include` of the new file.

When the referenced file is **renamed or moved** in the IDE, the `#include` path is updated automatically
to keep pointing at the file.

---

## Editor support

- **Syntax highlighting** for directives, macro names and inline `{#…}` references
- **Completion** of directive keywords, of `#define`s and value-bearing predefined variables, and of
  built-in functions
- **Validation** of unknown directives, unknown `{#…}` references and never-used `#define`s
- **Rename** and **Find Usages** across a `#define` and all its `{#Name}` usages
- **Inline** and **Extract** intentions for `#include`, with automatic path updates on rename/move
