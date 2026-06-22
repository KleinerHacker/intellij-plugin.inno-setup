# `#include` / `#file`

`#include` pastes the contents of another file into the script at compile time, and `#file` reads a file
and exposes its content for use by the preprocessor. Both bring external content into a script.

---

## Syntax

```ini
#include "filename.iss"
#include <filename.iss>
#file "data.txt"
```

- `#include "file"` resolves relative to the including script (absolute paths are used as-is); the
  angle-bracket form searches the configured include path.
- `#file "file"` reads the file and returns a temporary file name that other directives can reference.

---

## Working with `#include`

The plugin treats `#include` as a first-class reference:

- **Go to file** — **Ctrl+B** / **Cmd+B** jumps to the referenced file; the path is completed as you type.
- **Automatic path updates** — renaming or moving the target file in the IDE updates the `#include` path.
- **Inline `#include` content** — the **Alt+Enter** intention on an `#include` line replaces it with the
  verbatim content of the file (one level only). You are then asked whether to delete the now-inlined file.
- **Extract selection to `#include` file** — select lines and move them into a new file next to the
  current script; the selection is replaced with an `#include` of the new file.
- **Show Effective Script** — opens the fully include-resolved script in a read-only tab.

```ini
#include "common\\settings.iss"

[Setup]
AppName={#MyAppName}      ; defined inside the included file
```

---

## Validation

The `#include` line itself is checked: a missing or non-existent file, and a non-literal or empty path,
are flagged as errors. Problems detected **inside** an included file (unknown directives, flags, undefined
constants) are surfaced on the `#include` line of the including script, and required-section checks account
for content contributed by includes.

---

See the official
[`#include` :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_include.htm) and
[`#file` :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_file.htm) documentation.
