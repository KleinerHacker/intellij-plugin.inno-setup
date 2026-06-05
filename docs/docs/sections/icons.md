# [Icons]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=iconssection){
.md-button .md-button--primary }

The `[Icons]` section creates Windows shortcuts during installation — in the Start Menu, on the Desktop, or anywhere
else. Each entry creates exactly one shortcut. The target can be an executable, a document, a folder, or a URL. Use the
`Tasks` parameter to make shortcuts optional, letting the user decide on the *Select Additional Tasks* wizard page.

---

## Name

`string` · **Required**

Full path and name of the shortcut, e.g. `{group}\My Program` or `{commondesktop}\My Program`.

---

## Filename

`string` · **Required**

Target of the shortcut — an executable, document, folder, or URL.

---

## Parameters

`string`

Command-line arguments passed to the target when the shortcut is activated.

---

## WorkingDir

`string`

Working directory set when the shortcut is launched. Defaults to the directory containing the target.

---

## HotKey

`string`

Global keyboard shortcut to launch the target, e.g. `ctrl+alt+k`.

---

## Comment

`string`

Tooltip text shown when the user hovers over the shortcut.

---

## IconFilename

`string`

Path to an `.ico`, `.exe`, or `.dll` containing the icon for this shortcut.

---

## IconIndex

`integer`

Zero-based index of the icon within `IconFilename`. Defaults to `0`.

---

## AppUserModelID

`string`

Windows 7+ Application User Model ID, used to group taskbar buttons and associate toast notifications.

---

## AppUserModelToastActivatorCLSID

`string`

Windows 10+ COM CLSID for toast notification activation via this shortcut.

---

## Flags

`string` · **Multiple values**

Behavioural flags: `runminimized`, `runmaximized`, `closeonexit`, `createonlyiffileexists`, `preventpinning`.

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

Name of a Pascal procedure in `[Code]` called immediately before this shortcut is created.

---

## AfterInstall

`string`

Name of a Pascal procedure in `[Code]` called immediately after this shortcut is created.

---

## MinVersion

`string`

Minimum Windows version for which this entry applies. Use `0` to never apply.

---

## OnlyBelowVersion

`string`

Maximum Windows version (exclusive) for which this entry applies. Use `0` for no upper limit.
