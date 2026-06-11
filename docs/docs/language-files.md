# Inno Setup Language Files

The plugin supports Inno Setup language files (`.isl`) in addition to installer scripts (`.iss`). Language files use the
same editor infrastructure as scripts, but only language-specific sections are valid.

---

## Supported Sections

`.isl` files may contain:

- `[LangOptions]` for language metadata, locale identifiers, fonts, and text direction
- `[Messages]` for translated built-in installer messages
- `[CustomMessages]` for translated project-specific messages referenced through `{cm:...}`

`[Setup]` is not required in `.isl` files. Instead, `[LangOptions]` must define `LanguageName` and `LanguageID`.

---

## Language-Aware Editing

Language identifiers are backed by the Windows LCID data bundled with the plugin. Completion for `LanguageID` shows the
locale name, flag icon, and hexadecimal identifier, with Inno Setup's built-in languages sorted first.

Message keys can use a language prefix such as `german.WelcomeLabel1`. Prefixes resolve to `[Languages]` entries in the
script, support rename/find usages, and display language flag inlays when the referenced language can be resolved.

