# [LangOptions]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=langoptionssection){ .md-button .md-button--primary }

The `[LangOptions]` section defines language-specific display settings. It is required in `.isl` language files and can
also be used in scripts to override options for a declared language. In scripts, directive names may be prefixed with a
language name, e.g. `german.DialogFontName=Segoe UI`.

---

## LanguageName

`string` · **Required in .isl**

Native name of the language as shown in the language selection dialog, e.g. `Deutsch`.

---

## LanguageID

`integer` · **Required in .isl**

Windows language identifier used for automatic language detection, usually written as Pascal-style hexadecimal, e.g.
`$0409` for English (United States) or `$0407` for German (Germany). Completion uses the bundled Windows LCID list.

---

## LanguageCodePage

`integer`

Code page used to convert non-Unicode text in the message file. Use `0` when the file contains only Unicode or ASCII
text.

---

## DialogFontName

`string`

Font used for most wizard text. Defaults to Segoe UI when left empty.

---

## DialogFontSize

`integer`

Point size of the dialog font. Default: `9`.

---

## DialogFontBaseScaleWidth

`integer`

Base width, in pixels, used to scale dialog controls relative to the dialog font. Default: `7`.

---

## DialogFontBaseScaleHeight

`integer`

Base height, in pixels, used to scale dialog controls relative to the dialog font. Default: `15`.

---

## WelcomeFontName

`string`

Font used for the large heading on the Welcome and Setup Completed pages.

---

## WelcomeFontSize

`integer`

Point size of the welcome font. Default: `14`.

---

## RightToLeft

`integer`

Set to `1` for right-to-left languages, or `0` for left-to-right languages.

---

## Removed Directives

`TitleFontName`, `TitleFontSize`, `CopyrightFontName`, and `CopyrightFontSize` were removed in Inno Setup 6.4. The
plugin keeps them marked with their removal version for compatibility with older scripts.

