# [InstallDelete]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=installdeletesection){
.md-button .md-button--primary }

The `[InstallDelete]` section lists files and directories that Inno Setup removes at the *beginning* of installation,
before any new files are copied. This is useful for cleaning up stale files or old directory structures left behind by
previous versions of the application that the new installer no longer tracks.

---

## Type

`string` · **Required**

What to delete: `files` (matching files only), `filesandordirs` (files and all subdirectories), `dirifempty` (the
directory only if it contains no files).

---

## Name

`string` · **Required**

Path or wildcard pattern of the file or directory to delete, e.g. `{app}\*.log` or `{app}\cache`.

---

## Components

`→ Components` · **Multiple values**

This entry is processed only when at least one of the listed components is selected.

---

## Tasks

`→ Tasks` · **Multiple values**

This entry is processed only when at least one of the listed tasks is checked.

---

## Languages

`→ Languages` · **Multiple values**

Limits this entry to the specified languages.

---

## Check

`string`

Name of a Pascal function in `[Code]` that returns `Boolean`. The entry is processed only when the function returns
`True`.

---

## MinVersion

`string`

Minimum Windows version for which this entry applies. Use `0` to never apply.

---

## OnlyBelowVersion

`string`

Maximum Windows version (exclusive) for which this entry applies. Use `0` for no upper limit.
