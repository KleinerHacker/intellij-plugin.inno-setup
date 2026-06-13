# [Languages]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=languagessection){ .md-button .md-button--primary }

The `[Languages]` section declares all languages available in the installer. Each entry points to an ISL message file
that provides the translated strings for the wizard UI. The first entry in the list becomes the default language.
Languages defined here can be referenced in other sections via the `Languages` common parameter to restrict entries to
specific locales.

---

## Name

`string` · **Required**

Internal language identifier, e.g. `english`, `german`. Referenced by the `Languages` parameter in other sections.

---

## MessagesFile

`string` · **Required**

Path to the ISL message file. Use `compiler:Default.isl` for the built-in English messages, or
`compiler:Languages\German.isl` for one of the bundled translations.
