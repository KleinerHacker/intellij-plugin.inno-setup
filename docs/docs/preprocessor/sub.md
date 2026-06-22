# `#sub` / `#endsub`

`#sub` begins a **subroutine** — a named block of directives that can be invoked repeatedly — and `#endsub`
ends it. A subroutine is most often used as the body executed by a [`#for`](for.md) loop.

---

## Syntax

```ini
#sub Name
  ; directives …
#endsub
```

---

## Description

Everything between `#sub Name` and `#endsub` is stored under `Name` and executed each time the subroutine is
invoked (for example via the `Func` parameter of `#for`). Inside the subroutine the current loop variable is
available, so each invocation can emit something different:

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

Every `#sub` must be closed by a matching `#endsub`.

---

## Editor support

Both directive keywords are highlighted, completed (after `#`) and validated against the bundled ISPP
specification.

---

See the official [`#sub` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_sub.htm).
