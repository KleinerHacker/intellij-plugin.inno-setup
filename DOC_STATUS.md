# Inno Setup Documentation Coverage Status

> **Reference version:** Inno Setup 7.0.1-beta  
> **Docs URL:** https://jrsoftware.org/ishelp/  
> **Last checked:** 2026-06-08  
>
> This file tracks which parts of the official Inno Setup documentation are implemented in the plugin's
> spec files (`src/main/resources/spec/`) and language support. Update the "Last checked" date and the
> status entries whenever the Inno Setup docs change or the plugin catches up.
>
> **Legend:** ✅ implemented · ⚠️ partial · ❌ missing · 🗑️ obsolete (skip)

---

## Sections

| Section         | Type       | Status | Notes                                        |
|-----------------|------------|--------|----------------------------------------------|
| `[Setup]`       | directive  | ✅     | All ~158 current attributes covered          |
| `[Types]`       | parameter  | ✅     | All attributes + flags covered               |
| `[Components]`  | parameter  | ✅     | All attributes + flags covered               |
| `[Tasks]`       | parameter  | ✅     | All attributes + flags covered               |
| `[Dirs]`        | parameter  | ✅     | All attributes + flags covered               |
| `[Files]`       | parameter  | ✅     | All attributes + flags covered               |
| `[Icons]`       | parameter  | ✅     | All attributes + flags covered               |
| `[INI]`         | parameter  | ❌     | Entirely missing from spec                   |
| `[Registry]`    | parameter  | ✅     | All attributes + flags covered               |
| `[Run]`         | parameter  | ✅     | All attributes + flags covered               |
| `[UninstallRun]`| parameter  | ✅     | All attributes + flags covered               |
| `[Languages]`   | parameter  | ✅     | All attributes covered                       |
| `[Messages]`    | directive  | ✅     | Full standard `Default.isl` message set; `lang.` prefix completion |
| `[CustomMessages]`| directive| ✅     | No predefined names; `lang.` prefix completion + `{cm:}` reference/find-usages/rename |
| `[LangOptions]` | directive  | ✅     | All directives covered; TitleFont*/CopyrightFont* removed in 6.4; LanguageID completion |
| `[InstallDelete]`| parameter | ✅     | All attributes covered                       |
| `[UninstallDelete]`| parameter| ✅    | All attributes covered                       |
| `[ISSigKeys]`   | parameter  | ❌     | Entirely missing (added ~IS 6.x)             |
| `[Code]`        | code       | ✅     | Treated as injected Pascal, no spec needed   |

---

## [Setup] Section — Attribute Coverage

All current (non-obsolete) `[Setup]` directives from the official docs are now present in
`isi-spec.yaml` (166 entries, incl. the legacy `EncryptionKey`/`EncryptionKeyDerivation` aliases).
The obsolete directives below are intentionally skipped.

### Obsolete (intentionally skipped)
🗑️ `AlwaysCreateUninstallIcon`, `BackColor`, `BackColor2`, `BackColorDirection`, `BackSolid`,
`DisableAppendDir`, `DontMergeDuplicateFiles`, `MessagesFile`, `UninstallIconFile`,
`UninstallIconName`, `UninstallStyle`, `WindowResizable`, `WindowShowCaption`,
`WindowStartMaximized`, `WindowVisible`, `WizardResizable`

---

## Missing Sections — Details

### `[INI]` section
Allows Setup to modify `.ini` files on the user's system.  
**Attributes:** `Filename` (req), `Section` (req), `Key`, `String`  
**Flags:** `createkeyifdoesntexist`, `uninsdeleteentry`, `uninsdeletesection`, `uninsdeletesectionifempty`  
**Common params:** `Components`, `Tasks`, `Languages`, `Check`, `MinVersion`, `OnlyBelowVersion`

### `[Messages]` section ✅
Overrides installer message strings from `Default.isl`.  
**Format:** `MessageID=Text` or `lang.MessageID=Text`.  
**Implemented:** directive section in `isi-spec.yaml` with `internationalization: true` and the full
standard set of `Default.isl` message identifiers (~273) as known keys. Key completion offers a
language-prefix list (flag icon + language name, from the file's `[Languages]` or the built-in
languages) followed by the known message identifiers; after a `lang.` prefix only the message
identifiers are completed. See `MessagesKeyProvider`.

### `[CustomMessages]` section ✅
Defines custom localizable strings usable via the `{cm:…}` constant.  
**Format:** Same as `[Messages]` — user-chosen key/value (no predefined names).  
**Implemented:** directive section in `isi-spec.yaml` with `internationalization: true` and an empty
attribute list (`internationalization` flag in the spec/schema/`IsiSectionSpec`). The same
language-prefix completion applies. Each declaration is a renamable named element
(`IsiDirectiveEntryMixinImpl`); `{cm:MessageName}` resolves to it (`IsiCustomMessageReference`,
red-highlighted when unresolved), offers completion of declared names
(`CustomMessageAfterCmProvider`), supports Find Usages, and Rename updates the declaration(s)
— including other-language variants — and all `{cm:}` usages
(`IsiCustomMessageReferencesSearcher`).

### `[LangOptions]` section ✅
Language-specific display settings (used inside `.isl` files and overridable in scripts). Implemented
as a directive section in `isi-spec.yaml`.  
**Attributes:** `LanguageName`, `LanguageID`, `LanguageCodePage`, `DialogFontName`, `DialogFontSize`,
`DialogFontBaseScaleWidth`, `DialogFontBaseScaleHeight`, `WelcomeFontName`, `WelcomeFontSize`, `RightToLeft`  
**Removed in 6.4:** `TitleFontName`, `TitleFontSize`, `CopyrightFontName`, `CopyrightFontSize` (marked `until: "6.4"`)  
**Extras:** all Windows locales live in `isl-code.yaml` (loaded by `IssWindowsLanguageService`); each
carries an English name, a flag (reused or generated) and a `builtin` flag marking the Inno-bundled
languages (which also provide `issName` + `messagesFile`). `LanguageID` completion lists every locale
(name + flag + greyed `$hex` id), sorting built-in languages to the top with an "Inno built-in" marker;
a value that is neither `0` nor a listed LCID is flagged as a warning. The `[Languages]` Name/MessagesFile
completion is fed from the `builtin` languages. The Windows `LanguageID` is the single source of truth
for a language's flag and English name: `IssLanguageIdService` resolves a `[Languages] MessagesFile`
(bundled `compiler:` files from the Inno Setup directory, custom files from SourceDir) and reads its
`[LangOptions] LanguageID`, falling back to the bundled `messagesFile → LCID` mapping when the file is
unavailable. Inlays show flag + English name before `[Languages] MessagesFile`, and the referenced
language's flag before a `lang.` message key. See `IsiLanguageInlayHintsProvider`. (The former
Name↔MessagesFile consistency warning was removed.)

### `[ISSigKeys]` section
Declares public keys for `.issig` file-signature verification (used with `issigverify` flag in `[Files]`).  
**Attributes:** `Name` (req), `KeyFile`, `PublicX`, `PublicY`, `KeyID`, `Group`, `RuntimeID`

---

## Constants (`isi-const.yaml`)

Coverage appears **complete** for the constants documented at the time of last check (58 entries including
deprecated). Notable items already marked deprecated/removed in `isi-const.yaml`:
- `{hwnd}` — removed in Inno Setup 6.4 ✅ annotated
- `{pf}`, `{pf32}`, `{pf64}`, `{cf}`, `{cf32}`, `{cf64}`, `{fonts}`, `{sendto}` — deprecated ✅ annotated

---

## ISPP Preprocessor (`iss-ispp.yaml`)

Coverage appears **complete**: 24 directives, 13+ predefined variables, 31+ builtin functions.
All standard directives (`#define`, `#undef`, `#if`/`#elif`/`#else`/`#endif`, `#ifdef`, `#ifndef`,
`#include`, `#for`, `#sub`, `#endsub`, `#emit`, `#expr`, `#pragma`, `#error`, etc.) are present.

---

## IDE Features

| Feature                          | Status | Notes                                        |
|----------------------------------|--------|----------------------------------------------|
| Syntax highlighting (ISI)        | ✅     |                                              |
| Syntax highlighting (ISPP)       | ✅     |                                              |
| Code completion — sections       | ✅     |                                              |
| Code completion — attributes     | ✅     |                                              |
| Code completion — flags          | ✅     |                                              |
| Code completion — constants      | ✅     |                                              |
| Code completion — ISPP directives| ✅     |                                              |
| Code completion — `[Languages]`  | ✅     | Built-in names + MessagesFile, with flag icons|
| Code completion — `LanguageID`   | ✅     | Windows LCIDs: name + flag + greyed `$hex` id |
| Code completion — message i18n prefix | ✅ | `[Messages]`/`[CustomMessages]`: `lang.` prefix list (declared `[Languages]` only; flag + name from LanguageID) + message ids |
| Code completion — `{cm:…}`       | ✅     | Declared custom-message names                 |
| `{cm:…}` reference / find usages / rename | ✅ | Resolves to `[CustomMessages]`; red when unresolved; rename keeps language variants in sync; `cm` rendered italic |
| `lang.` prefix reference / find usages / rename | ✅ | Prefix resolves to a `[Languages] Name`; red when undeclared; renaming the Name updates all prefixes |
| Inlay hints — language flags     | ✅     | Flag + English name (from the LanguageID in the referenced `.isl`) before `[Languages] MessagesFile` and `[LangOptions] LanguageID`; flag of the referenced language before a `lang.` message key |
| Brace matching `[]`, `{}`, `()`  | ✅     |                                              |
| Code folding                     | ✅     |                                              |
| Structure view                   | ✅     |                                              |
| Documentation popup              | ✅     | From spec YAML descriptions                  |
| Find usages                      | ✅     | For ISPP `#define` references                |
| Rename refactoring               | ✅     | For ISPP identifiers                         |
| Commenter (`Ctrl+/`)             | ✅     |                                              |
| Quote handler                    | ✅     |                                              |
| ISPP language injection          | ✅     | Preprocessor lines injected into ISI         |
| Semantic annotations / errors    | ✅     |                                              |
| [Code] section Pascal support    | ❌     | No Pascal intellisense; treated as plain text|
