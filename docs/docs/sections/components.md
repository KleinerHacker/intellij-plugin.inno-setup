# [Components]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=componentssection){ .md-button .md-button--primary }

The `[Components]` section defines the selectable features shown on the wizard's Select Components page. Components can be organised into a parent/child hierarchy using backslash notation, e.g. `extra\plugins`. Entries in `[Files]`, `[Icons]`, and other sections are linked to components via their `Components` parameter, so only the files belonging to selected components are installed.

---

## Name

`string` · **Required**

Internal identifier for this component. Use backslash notation for hierarchy, e.g. `main\help`.

---

## Description

`string` · **Required**

Label shown for this component in the wizard's component selection list.

---

## Types

`→ Types` · **Multiple values**

Space-separated list of installation type names (from `[Types]`) that include this component by default.

---

## ExtraDiskSpaceRequired

`integer`

Additional disk space in bytes that this component requires beyond the files it installs. Displayed on the Select Components page.

---

## Flags

`string` · **Multiple values**

Behavioural flags: `fixed`, `checkablealone`, `exclusive`, `restart`, `dontinheritcheck`, `disablenouninstallwarning`.

---

## Check

`string`

Name of a Pascal function in `[Code]` that returns `Boolean`. The entry is processed only when the function returns `True`.

---

## Languages

`→ Languages` · **Multiple values**

Limits this entry to the specified languages.
