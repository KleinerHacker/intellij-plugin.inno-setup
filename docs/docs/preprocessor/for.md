# `#for`

`#for` repeats a directive a number of times, evaluating a C-style loop header. It is the preprocessor's
way to generate repetitive script content — for example one entry per item in an [array](arrays.md).

---

## Syntax

```ini
#for {Init; Condition; Increment} Directive
```

The header has three parts, separated by semicolons: an initialization, a condition checked before each
iteration, and an increment evaluated after each iteration — exactly like a C `for` loop.

---

## Description

On each iteration the trailing directive is executed. Combined with [`#emit`](output.md) or an
[array](arrays.md), `#for` generates a series of script lines:

```ini
#dim Langs[3]
#define Langs[0] "en"
#define Langs[1] "de"
#define Langs[2] "fr"

[Languages]
#for {i = 0; i < 3; i++} \
  #emit "Name: """ + Langs[i] + """; MessagesFile: ""compiler:Languages\\" + Langs[i] + ".isl"""
```

The backslash continues the directive on the next line (see [`#pragma spansymbol`](pragma.md) to change the
continuation character).

---

## Editor support

The directive keyword is highlighted, completed (after `#`) and validated against the bundled ISPP
specification.

---

See the official [`#for` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_for.htm).
