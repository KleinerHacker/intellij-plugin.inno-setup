# Inno Setup Template Files

In addition to installer scripts (`.iss`) and language files (`.isl`), the plugin supports **Inno Setup
template files** (`.ist`). A template is a reusable, free-text fragment that is meant to be pulled into a
script through an ISPP `#include` directive. It lets you factor common sections, directives, or
preprocessor definitions out of individual scripts and share them across several installers.

---

## What a Template Is

A `.ist` file is **free text**. Unlike `.iss` and `.isl` files, it has no required structure and is never
validated as a standalone script:

- It may contain **whole sections** (`[Files]`, `[Registry]`, …), **partial sections**, loose directive
  lines, ISPP preprocessor directives (`#define`, `#include`, …), or any mixture of these.
- It may even contain content that would be invalid in a real `.iss` script — because a template is only a
  building block, not a complete installer. The plugin therefore **never reports parse errors or
  validation warnings** in a `.ist` file.
- Its meaning is determined only once it is included into a host script, where it is interpreted in the
  context of the surrounding `.iss` content.

This mirrors how the Inno Setup preprocessor (ISPP) treats `#include` targets: the included text is spliced
into the script as-is, so a fragment is only ever as correct as the place it is included into.

---

## Using a Template

Templates are consumed through `#include`. Because the file extension is irrelevant to ISPP, a `.ist`
template is included exactly like any other file:

```ini
; main.iss
[Setup]
AppName=My Program
AppVersion=1.5

#include "common-files.ist"
#include "registry-defaults.ist"
```

```ini
; common-files.ist  (a template, free text)
[Files]
Source: "readme.txt"; DestDir: "{app}"
Source: "license.txt"; DestDir: "{app}"
```

When the host script is analysed, the plugin resolves the `#include` and merges the template's sections
into the **effective script** — the fully include-resolved view of `main.iss`. Same-named sections from the
host and from included templates are unified into a single block, so the example above behaves as if the
`[Files]` entries had been written directly inside `main.iss`.

Templates may include other templates (or scripts) transitively; include cycles are detected and broken
safely.

---

## Editing Features

### Syntax Highlighting

Templates use a dedicated highlighter so that the familiar Inno Setup structure stays readable even in
free-text fragments:

- **Section headers** (`[Files]`, `[Registry]`, …) are highlighted as structural markers
- **Preprocessor lines** (`#define`, `#include`, …) are recognised as the only structured element and are
  highlighted via the full ISPP language injection (see below)

### Preprocessor Injection

Every `#…` line in a template carries an **injected ISPP fragment**, exactly as in `.iss`/`.isl` files.
Inside those lines you get the complete preprocessor language support — completion, references, and inline
documentation for `#define`, `#include`, expressions, and built-in functions.

### Section-Name Completion

Typing `[` opens a completion popup offering **all known Inno Setup section names**. Because a template is
free text with no file-type or version filtering, the full set of sections is always suggested (unlike the
context-restricted lists in `.iss`/`.isl`). Accepting a suggestion inserts the closing `]` automatically.

### Brace Matching

Matching is provided for `[ ]` (structural section brackets) and `( )`, so navigating and balancing
template fragments works the same way as in scripts.

### Include-Path Completion & Navigation

Inside an `#include "…"` directive, `.ist` templates are offered for completion alongside other includable
files, and the path resolves to the template file for **go-to-definition** navigation.

---

## What Templates Deliberately Do *Not* Have

Because a `.ist` file is an intentionally unstructured fragment, several script-only features are switched
off for it:

- **No validation / annotator** — broken or out-of-context Inno Setup content never produces ERROR or
  WARNING highlights.
- **No structure view** — a template has no guaranteed section tree to display.

These features remain available on the host `.iss`/`.isl` script, where the template's content is analysed
in its real, include-resolved context.

---

## Creating a Template

Use **New → Inno Setup Template** from the IDE's *New File* menu. Since a template is free text, the dialog
only asks for a file name and creates an empty `.ist` file — no content skeleton is generated.
