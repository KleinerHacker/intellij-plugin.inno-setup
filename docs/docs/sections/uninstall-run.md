# [UninstallRun]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=uninstallrunsection){
.md-button .md-button--primary }

The `[UninstallRun]` section works exactly like `[Run]`, but its entries are executed at the *start* of uninstallation
rather than after installation. Use it to stop services, kill running processes, or clean up state that cannot be
handled by simply deleting files and registry keys. Entries run sequentially in the order they appear.

---

## Filename

`string` · **Required**

Path to the executable, document, or folder to launch during uninstallation.

---

## Description

`string`

Label for an optional checkbox shown on the post-uninstall page. Requires the `postinstall` flag.

---

## Parameters

`string`

Command-line arguments passed to `Filename`.

---

## WorkingDir

`string`

Working directory for the launched process. Defaults to the directory containing `Filename`.

---

## StatusMsg

`string`

Status message displayed in the progress window while this entry is executing.

---

## RunOnceId

`string`

Unique identifier preventing this entry from executing more than once across multiple uninstall runs.

---

## Verb

`string`

Shell verb used with the `shellexec` flag, e.g. `open`, `print`.

---

## OnLog

`string`

Name of a Pascal procedure in `[Code]` called for each line of output (requires the `logoutput` flag).

---

## Flags

`string` · **Multiple values**

Behavioural flags: `postinstall`, `shellexec`, `nowait`, `runhidden`, `skipifsilent`, `skipifnotsilent`, `unchecked`,
`waituntilterminated`, `waituntilidle`, `logoutput`, `runasoriginaluser`.

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
