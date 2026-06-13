# Inno Setup Language Files

The plugin supports Inno Setup language files (`.isl`) in addition to installer scripts (`.iss`). Language files use the
same editor infrastructure as scripts, but only language-specific sections are valid.

![Inno Setup Language File Editor](assets/images/isl.png)

---

## Supported Sections

`.isl` files may contain:

- `[LangOptions]` for language metadata, locale identifiers, fonts, and text direction
- `[Messages]` for translated built-in installer messages
- `[CustomMessages]` for translated project-specific messages referenced through `{cm:...}`

`[Setup]` is not required in `.isl` files. Instead, `[LangOptions]` must define `LanguageName` and `LanguageID`.

---

## Editing Features

### Syntax Highlighting

The same colour scheme used in `.iss` files applies to `.isl` files:

- **Section headers** (`[LangOptions]`, `[Messages]`, `[CustomMessages]`) are highlighted as structural markers
- **Directive keys** and their values are coloured distinctly
- **Message keys** stand out from their translated string values

### Code Completion

Context-aware suggestions are offered inside `.isl` files:

- Known `[LangOptions]` directive keys with their accepted value types
- `LanguageID` values are offered with a **flag icon**, locale name, and hexadecimal LCID — Inno Setup's
  built-in languages appear first in the list

### Inlay Hints

`LanguageID` values are annotated inline with the matching **flag icon and locale name**
(e.g. *Dutch (Netherlands) $0413*), making the language immediately recognisable without looking up the
hexadecimal identifier in external documentation.

### Inline Documentation

Hovering over any `[LangOptions]` key shows its description from the bundled spec, including accepted values
and remarks.

### Validation

The annotator highlights problems in `.isl` files:

- Unknown or misspelled `[LangOptions]` keys
- Invalid or unrecognised `LanguageID` values
- Missing required directives in `[LangOptions]`

### Language-Prefix References

Message keys can carry a language prefix such as `german.WelcomeLabel1` when used inside a `.iss` script.
These prefixes resolve to `Name:` entries in `[Languages]` and fully participate in:

- **Go-to-definition** — jump from the prefix to its `[Languages]` declaration
- **Find Usages** — find all places that reference a particular language name
- **Rename refactoring** — rename a language name and update all prefixed usages consistently
