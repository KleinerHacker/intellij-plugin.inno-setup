# [Types]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=typessection){ .md-button .md-button--primary }

The `[Types]` section defines the named installation profiles shown in the wizard's Select Components page — for example *Full*, *Compact*, and *Custom*. Each component in `[Components]` references one or more types to declare which profiles include it by default. Exactly one type can be marked as the user-customisable type using the `iscustom` flag.

---

## Name

`string` · **Required**

Internal identifier for this installation type. Referenced by the `Types` parameter in `[Components]`.

---

## Description

`string` · **Required**

Human-readable label displayed in the wizard for this installation type.

---

## Flags

`string` · **Multiple values**

Behavioural flags. `iscustom` marks this type as the user-customisable type — only one type per script may carry this flag.
