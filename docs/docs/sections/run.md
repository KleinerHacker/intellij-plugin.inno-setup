# [Run]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=runsection){ .md-button
.md-button--primary }

The `[Run]` section lists programs or files that Inno Setup executes after a successful installation, just before the
final wizard page is shown. Entries run sequentially in the order they appear. You can launch executables silently in
the background, open documents with their associated application, or present the user with optional post-install
actions (like *Launch MyApp*) via checkboxes on the completion page.

---

## Filename

`string` · **Required**

Path to the executable, document, or folder to launch. Use the `shellexec` flag to open non-executable files with their
associated application.

---

## Description

`string`

Label for an optional checkbox shown on the post-install completion page. Requires the `postinstall` flag.

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

Unique identifier that prevents this entry from running more than once across repeated installs.

---

## Verb

`string`

Shell verb used with the `shellexec` flag, e.g. `open`, `print`.

---

## OnLog

`string` · **Since 6.6**

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
