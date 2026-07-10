# Code Style

The plugin formats `.iss` / `.isl` scripts with **Reformat Code** (<kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>L</kbd>).
The rules live under **Settings ▸ Editor ▸ Code Style ▸ Inno Setup** and are all enabled by default.

---

## Rules

| Rule                                     | Effect                                                                                     | Example                                        |
|------------------------------------------|--------------------------------------------------------------------------------------------|------------------------------------------------|
| **Exactly one blank line between sections** | Collapses (or inserts) the gap between two `[Section]` blocks to a single blank line.    | `…value`⏎⏎⏎`[Files]` → `…value`⏎⏎`[Files]`     |
| **No spaces inside section brackets**    | Tightens the brackets around the section name.                                             | `[ Setup ]` → `[Setup]`                        |
| **Space around `=` in directives**       | One space on each side of `=` in a `Key = Value` directive.                                | `AppName=My App` → `AppName = My App`          |
| **Space after `:` in parameters**        | No space before, one space after `:` in a `Key: Value` parameter.                          | `DestDir:"{app}"` → `DestDir: "{app}"`         |
| **Space after `;` between parameters**   | No space before, one space after the `;` separating parameter pairs.                       | `"a" ;DestDir` → `"a"; DestDir`                |
| **No leading whitespace before keys**    | A key / parameter / header line starts at column 0.                                        | `····AppName = A` → `AppName = A`              |
| **Space around arithmetic operators**    | One space around `+ - * / %` in preprocessor expressions (binary operators only).          | `#define X 2+3*4` → `#define X 2 + 3 * 4`      |

Each rule can be turned off individually on the settings page; the *space around `=`*, `:`, `;`, brackets and
leading-space options appear under **Spacing**, the blank-line option under **Blank Lines**.

---

## What is left untouched

- The **`[Code]`** section (Pascal Script) and everything after it — its indentation and layout are preserved.
- Values themselves — only the whitespace **around** the separators is normalized, never the value text.
- **Unary** signs (`-3`), a `-` inside identifiers or version strings, and the contents of quoted strings are
  not treated as arithmetic operators.

---

## See also

See [Editor](settings-editor.md) for the editor presentation options and [Settings](settings.md) for the
installation directory and version validation.
