# File Types

The plugin registers three dedicated file types within the IntelliJ platform. Each file type gets its own
icon, language infrastructure, and editor feature set.

![Inno Setup File Types](assets/images/filetypes.png)

---

## Overview

| File type               | Extension | Purpose                                                         |
|-------------------------|-----------|-----------------------------------------------------------------|
| **Inno Setup Script**   | `.iss`    | Main installer definition — sections, files, registry, code     |
| **Inno Setup Language** | `.isl`    | Translated messages and locale options for a single language    |
| **Inno Setup Template** | `.ist`    | Reusable free-text fragment pulled into a script via `#include` |

All file types are automatically recognised by their extension. No manual association is required.

---

## Inno Setup Script (`.iss`)

`.iss` files are the primary Inno Setup format. They describe the complete installer: which files are
packaged, which registry keys are written, which languages are offered, and — optionally — Pascal Script
runtime logic in the `[Code]` section. ISPP preprocessor directives (`#define`, `#include`, …) may appear
at the top of the file.

See [Script Files](script-files.md) for the full list of supported sections and editing features.

---

## Inno Setup Language (`.isl`)

`.isl` files supply translated strings for a single locale. They are referenced from a `.iss` script via
the `MessagesFile:` parameter in `[Languages]` and may override any subset of the built-in Inno Setup
messages. Custom project-specific messages can also be placed in `[CustomMessages]`.

See [Language Files](language-files.md) for the full list of supported sections and editing features.

---

## Inno Setup Template (`.ist`)

`.ist` files are reusable, free-text fragments meant to be pulled into a script through an ISPP `#include`
directive. A template may hold whole sections, partial sections, loose directive lines, or preprocessor
definitions — and, because it is only a building block rather than a complete installer, it is never
validated on its own. Its content is analysed only once it is included into a host `.iss`/`.isl` script.

See [Template Files](template-files.md) for the full description and editing features.

---

## Relationship Between File Types

A `.iss` script can reference one or more `.isl` files through its `[Languages]` section:

```ini
[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german";  MessagesFile: "compiler:Languages\German.isl"
```

The plugin resolves these references across files: language names declared in `[Languages]` are the
targets for language-prefix references (e.g. `german.WelcomeLabel1`) used inside `[Messages]` and
`[CustomMessages]`, and for `{cm:…}` constants inside values throughout the script.

A `.iss` (or `.isl`) script can additionally pull in any number of `.ist` templates through `#include`:

```ini
#include "common-files.ist"
```

The plugin resolves the include and merges the template's sections into the script's effective,
include-resolved view, so the shared fragment behaves as if it had been written inline.
