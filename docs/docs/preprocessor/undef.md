# `#undef`

`#undef` removes a macro that was previously declared with [`#define`](define.md). After it, the name is no
longer defined: `defined(Name)` becomes false and any later use of the name is treated as undefined.

---

## Syntax

```ini
#undef Name
```

---

## Description

`#undef` is typically paired with a conditional to redefine a value or to clear a feature flag:

```ini
#define EnableLogging
; … later …
#undef EnableLogging      ; the feature flag is gone from here on

#ifdef EnableLogging
  ; not emitted anymore
#endif
```

Undefining a name that was never defined has no effect. `#undef` is most useful together with the
[conditional directives](conditionals.md), which test whether a name is currently defined.

---

## Editor support

The directive keyword is highlighted, completed (after `#`) and validated against the bundled ISPP
specification.

---

See the official [`#define` / `#undef` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_define.htm).
