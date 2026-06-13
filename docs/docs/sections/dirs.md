# [Dirs]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=dirssection){ .md-button .md-button--primary }

The `[Dirs]` section creates additional directories on the target machine during installation. In most cases you do not
need this section at all — directories required by files listed in `[Files]` are created automatically. Use `[Dirs]`
when you need to create an empty directory structure, set specific NTFS attributes, or configure ACL permissions on a
directory.

---

## Name

`string` · **Required**

Full path of the directory to create, e.g. `{app}\data`. Supports all Inno Setup directory constants.

---

## Attribs

`string` · **Multiple values**

File-system attributes to set on the directory: `readonly`, `hidden`, `system`, `notcontentindexed`.

---

## Permissions

`string` · **Multiple values**

ACL permissions to grant on the directory, e.g. `users-modify`, `everyone-readexec`. Avoid setting ACLs on top-level
system directories such as `{sys}` or `{commonpf}`.

---

## Flags

`string` · **Multiple values**

Behavioural flags: `deleteafterinstall`, `setntfscompression`, `uninsalwaysuninstall`, `uninsneveruninstall`,
`unsetntfscompression`.

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

Name of a Pascal procedure in `[Code]` called immediately before this directory is created.

---

## AfterInstall

`string`

Name of a Pascal procedure in `[Code]` called immediately after this directory is created.

---

## MinVersion

`string`

Minimum Windows version for which this entry applies. Use `0` to never apply.

---

## OnlyBelowVersion

`string`

Maximum Windows version (exclusive) for which this entry applies. Use `0` for no upper limit.
