# `#undef`

`#undef` removes a macro that was previously declared with [`#define`](define.md). After it, the name is no
longer defined: `defined(Name)` becomes false and any later use of the name is treated as undefined.

---

## Syntax

```ini
#undef [Scope] Name
```

An optional scope keyword — `public`, `protected` or `private` — may precede the name, mirroring
[`#define`](define.md).

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

- The directive keyword (and an optional scope keyword) is highlighted, completed (after `#`) and
  validated against the bundled ISPP specification.
- The name in `#undef Name` **resolves to its `#define`** — go-to-definition (**Ctrl+B** / **Cmd+B**),
  Find Usages (**Alt+F7**) and rename keep the `#define`, the `#undef` and all `{#Name}` usages in sync.
- After `#undef ` completion offers the scope keywords and the names of the macros defined earlier.
- An `#undef` whose name has **no matching `#define`** does nothing: its name is grayed out with a
  quick-fix to remove the directive.

---

See the official [`#define` /
`#undef` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_define.htm).
