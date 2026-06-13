# [Registry]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=registrysection){ .md-button .md-button--primary }

The `[Registry]` section creates, modifies, or deletes Windows registry keys and values during installation. It supports
all major registry value types and gives fine-grained control over what happens to registry data on uninstall via flags
such as `uninsdeletekey` and `uninsdeletekeyifempty`. Both 32-bit and 64-bit registry views can be targeted explicitly.

---

## Root

`string` · **Required**

Registry root hive: `HKCU`, `HKLM`, `HKCR`, `HKU`, `HKCC`, or `HKA` (auto, depending on install mode). Append `32` or
`64` to force a specific registry view, e.g. `HKLM64`.

---

## Subkey

`string` · **Required**

Registry key path relative to `Root`, e.g. `Software\My Company\My App`.

---

## ValueType

`string`

Type of the registry value to write: `none` (key only), `string`, `expandsz`, `multisz`, `dword`, `qword`, `binary`.

---

## ValueName

`string`

Name of the registry value. Leave empty to target the default value of the key.

---

## ValueData

`string`

Data to write. Use `{olddata}` to append to an existing value, or `{break}` as a line separator for `multisz` values.

---

## Permissions

`string` · **Multiple values**

ACL permissions to set on the key: `full`, `modify`, `read`.

---

## Flags

`string` · **Multiple values**

Behavioural flags: `createvalueifdoesntexist`, `deletekey`, `deletevalue`, `dontcreatekey`, `noerror`,
`preservestringtype`, `uninsclearvalue`, `uninsdeletekey`, `uninsdeletekeyifempty`, `uninsdeletevalue`.

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

## BeforeInstall

`string`

Name of a Pascal procedure in `[Code]` called immediately before this entry is processed.

---

## AfterInstall

`string`

Name of a Pascal procedure in `[Code]` called immediately after this entry is processed.

---

## MinVersion

`string`

Minimum Windows version for which this entry applies. Use `0` to never apply.

---

## OnlyBelowVersion

`string`

Maximum Windows version (exclusive) for which this entry applies. Use `0` for no upper limit.
