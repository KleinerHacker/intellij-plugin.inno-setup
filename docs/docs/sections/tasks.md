# [Tasks]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=taskssection){ .md-button .md-button--primary }

The `[Tasks]` section defines optional actions the user can enable or disable on the *Select Additional Tasks* wizard page — things like creating a desktop shortcut or adding a context menu entry. Tasks appear as checkboxes or, when marked `exclusive`, as radio buttons within a group. Entries in other sections are linked to tasks via their `Tasks` parameter.

---

## Name

`string` · **Required**

Internal identifier for this task. Use backslash notation for sub-tasks, e.g. `desktopicon\user`.

---

## Description

`string` · **Required**

Label shown next to the checkbox or radio button in the wizard.

---

## GroupDescription

`string`

Optional heading displayed above a group of related tasks.

---

## Components

`→ Components` · **Multiple values**

This task is only shown when at least one of the listed components is selected.

---

## Flags

`string` · **Multiple values**

Behavioural flags: `checkablealone`, `checkedonce`, `dontinheritcheck`, `exclusive`, `restart`, `unchecked`.

---

## Check

`string`

Name of a Pascal function in `[Code]` that returns `Boolean`. The entry is processed only when the function returns `True`.

---

## Languages

`→ Languages` · **Multiple values**

Limits this entry to the specified languages.
