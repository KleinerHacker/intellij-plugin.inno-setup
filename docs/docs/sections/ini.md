# [INI]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=inissection){
.md-button .md-button--primary }

!!! warning "Plugin support"
    `[INI]` is tracked as missing in the current plugin spec. The section is listed here for documentation coverage, but
    completion and validation support may be incomplete until the spec is implemented.

The `[INI]` section creates or updates entries in `.ini` files on the user's system during installation. It is useful
for legacy applications that still store settings in INI files instead of the registry or application data files.

---

## Filename

`string` · **Required**

Path to the `.ini` file to modify, e.g. `{app}\MyApp.ini` or `{win}\MyApp.ini`.

---

## Section

`string` · **Required**

Name of the INI section that contains the key.

---

## Key

`string`

Name of the INI key to create, update, or delete.

---

## String

`string`

Value written to the key.

---

## Flags

`string` · **Multiple values**

Behavioural flags: `createkeyifdoesntexist`, `uninsdeleteentry`, `uninsdeletesection`,
`uninsdeletesectionifempty`.

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

