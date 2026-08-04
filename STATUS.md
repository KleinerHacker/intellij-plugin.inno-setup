# Inno Setup Documentation Coverage Status

> **Reference version:** Inno Setup 7.0.1-beta  
> **Docs URL:** https://jrsoftware.org/ishelp/  
> **Last checked:** 2026-08-04 (computed `#define` values surfaced as an inlay hint, closing gap 7;
> macro parameters fully modelled and `/D` symbols resolved everywhere, closing
> gaps 8 and 9; 2026-08-03: built-in function calls validated against the parsed `signature`, closing
> gap 1; 2026-08-01: ISPP preprocessor support re-audited against the official ISPP docs; conditional compilation implemented, closing gaps 4, 5 and most of 7 — see
> [Known gaps](#known-gaps-ispp-audit-2026-08-01); 2026-06-15: parameter/flag completeness re-audited against
> the official section pages)
>
> This file tracks which parts of the official Inno Setup documentation are implemented in the plugin's
> spec files (`src/main/resources/spec/`) and language support. Update the "Last checked" date and the
> status entries whenever the Inno Setup docs change or the plugin catches up.
>
> **Legend:** ✅ implemented · ⚠️ partial · ❌ missing · 🗑️ obsolete (skip)

---

## File types

| Extension | Status | Notes                                                                                                                                                                                                                                                                                                                                                  |
|-----------|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `.iss`    | ✅      | Inno Setup script (`IsScriptFileType`). Full parsing/tooling.                                                                                                                                                                                                                                                                                          |
| `.isl`    | ✅      | Inno Setup language file (`IsLanguageFileType`, `language/file_type/lang/**`). Same ISS language/parser; only `[LangOptions]`, `[Messages]`, `[CustomMessages]` allowed (`languageFile: true` in `is-spec.yaml`), enforced by `IsLanguageAnnotator`. `[Setup]` not required; instead `[LangOptions]` with `LanguageName` + `LanguageID` are required. |

> **Spec model:** `required` and `deprecated` (in `is-spec.yaml`, `is-const.yaml`, `is-preprocessor.yaml`)
> are **file-type-scoped arrays** (`[iss]`, `[isl]`, `[iss, isl]` or `[]`), not booleans — a rule can
> apply to scripts, language files, or both. Modelled as `Set<IsSectionSpecTarget>`; the matching JSON
> schemas use the shared `fileTargets` definition.

---

## Sections

| Section             | Type      | Status | Notes                                                                                                                |
|---------------------|-----------|--------|----------------------------------------------------------------------------------------------------------------------|
| `[Setup]`           | directive | ✅      | All ~158 current attributes covered                                                                                  |
| `[Types]`           | parameter | ✅      | Attributes + `iscustom` flag + common params (`Languages`/`Check`/`MinVersion`/`OnlyBelowVersion`, added 2026-06-15) |
| `[Components]`      | parameter | ✅      | Attributes + flags + common params (`MinVersion`/`OnlyBelowVersion` added 2026-06-15)                                |
| `[Tasks]`           | parameter | ✅      | Attributes + flags + common params (`MinVersion`/`OnlyBelowVersion` added 2026-06-15)                                |
| `[Dirs]`            | parameter | ✅      | All attributes + flags covered                                                                                       |
| `[Files]`           | parameter | ✅      | All attributes + flags covered (18 missing flags added 2026-06-15, see below)                                        |
| `[Icons]`           | parameter | ✅      | All attributes + flags covered (`excludefromshowinnewinstall`/`uninsneveruninstall`/`useapppaths` added 2026-06-15)  |
| `[INI]`             | parameter | ✅      | All attributes + flags covered                                                                                       |
| `[Registry]`        | parameter | ✅      | All attributes + flags covered                                                                                       |
| `[Run]`             | parameter | ✅      | All attributes + flags covered (8 missing flags added 2026-06-15, see below)                                         |
| `[UninstallRun]`    | parameter | ✅      | All attributes + flags covered (8 missing flags added 2026-06-15)                                                    |
| `[Languages]`       | parameter | ✅      | All attributes covered (`LicenseFile`/`InfoBeforeFile`/`InfoAfterFile` added 2026-06-15)                             |
| `[Messages]`        | directive | ✅      | Full standard `Default.isl` message set; `lang.` prefix completion                                                   |
| `[CustomMessages]`  | directive | ✅      | No predefined names; `lang.` prefix completion + `{cm:}` reference/find-usages/rename                                |
| `[LangOptions]`     | directive | ✅      | All directives covered; TitleFont*/CopyrightFont* removed in 6.4; LanguageID completion                              |
| `[InstallDelete]`   | parameter | ✅      | All attributes covered                                                                                               |
| `[UninstallDelete]` | parameter | ✅      | All attributes covered                                                                                               |
| `[ISSigKeys]`       | parameter | ✅      | All attributes covered (IS 6.5+)                                                                                     |
| `[Code]`            | code      | ✅      | Treated as injected Pascal, no spec needed                                                                           |

---

## 2026-06-15 parameter/flag completeness audit

A full re-check against the official section pages found several gaps that were closed:

- **Common parameters** (`Languages`/`MinVersion`/`OnlyBelowVersion` are supported by *all* parameter
  sections; `Check` additionally by `[Types]`/`[Components]`/`[Tasks]`): added `MinVersion`/`OnlyBelowVersion`
  to `[Components]` and `[Tasks]`, and the full `Languages`/`Check`/`MinVersion`/`OnlyBelowVersion` set to
  `[Types]`.
- **`[Languages]`**: added `LicenseFile`, `InfoBeforeFile`, `InfoAfterFile` (per-language overrides of the
  matching `[Setup]` directives).
- **`[Files]` flags** added: `allowunsafefiles`, `dontverifychecksum`, `fontisnttruetype`, `gacinstall`,
  `noregerror`, `onlyifdestfileexists`, `overwritereadonly`, `promptifolder`, `sign`, `signonce`,
  `skipifsourcedoesntexist`, `solidbreak`, `sortfilesbyextension`, `sortfilesbyname`, `uninsneveruninstall`,
  `uninsnosharedfileprompt`, `uninsremovereadonly`, `uninsrestartdelete`.
- **`[Run]`/`[UninstallRun]` flags** added: `32bit`, `64bit`, `dontlogparameters`, `hidewizard`,
  `runascurrentuser`, `runmaximized`, `runminimized`, `skipifdoesntexist`.
- **`[Icons]` flags** added: `excludefromshowinnewinstall`, `uninsneveruninstall`, `useapppaths`.

- **`[InstallDelete]`/`[UninstallDelete]`**: added `BeforeInstall`/`AfterInstall` (per
  `topic_scriptinstall.htm` these are supported by every parameter section *except* `[Languages]`,
  `[Types]`, `[Components]`, `[Tasks]`).

`[Dirs]`, `[Registry]`, `[INI]`, `[ISSigKeys]` were verified complete with no changes needed.

### Authoritative cross-section parameter rules (per official docs)

These five parameters are not section-specific; their applicability is governed by dedicated doc pages:

| Parameter(s)                                  | Supported by                                                       | Source                            |
|-----------------------------------------------|--------------------------------------------------------------------|-----------------------------------|
| `Languages`, `MinVersion`, `OnlyBelowVersion` | all parameter sections                                             | `topic_commonparams.htm`          |
| `Check`                                       | all parameter sections                                             | `topic_scriptcheck.htm`           |
| `Components`                                  | all **except** `[Types]`, `[Components]`                           | `topic_componentstasksparams.htm` |
| `Tasks`                                       | all **except** `[Types]`, `[Components]`, `[Tasks]`                | `topic_componentstasksparams.htm` |
| `BeforeInstall`, `AfterInstall`               | all **except** `[Languages]`, `[Types]`, `[Components]`, `[Tasks]` | `topic_scriptinstall.htm`         |

(`[Languages]` itself only exposes `Name`, `MessagesFile`, `LicenseFile`, `InfoBeforeFile`,
`InfoAfterFile` — it does not take the common/conditional parameters.)

---

## [Setup] Section — Attribute Coverage

All current (non-obsolete) `[Setup]` directives from the official docs are now present in
`is-spec.yaml` (166 entries, incl. the legacy `EncryptionKey`/`EncryptionKeyDerivation` aliases).
The obsolete directives below are intentionally skipped.

### Obsolete (intentionally skipped)

🗑️ `AlwaysCreateUninstallIcon`, `BackColor`, `BackColor2`, `BackColorDirection`, `BackSolid`,
`DisableAppendDir`, `DontMergeDuplicateFiles`, `MessagesFile`, `UninstallIconFile`,
`UninstallIconName`, `UninstallStyle`, `WindowResizable`, `WindowShowCaption`,
`WindowStartMaximized`, `WindowVisible`, `WizardResizable`

---

## Missing Sections — Details

### `[INI]` section ✅

Allows Setup to create or modify `.ini` files on the user's system. Implemented as a parameter section
in `is-spec.yaml`.  
**Attributes:** `Filename` (optional, defaults to `WIN.INI`), `Section` (req), `Key`, `String`  
**Flags:** `createkeyifdoesntexist`, `uninsdeleteentry`, `uninsdeletesection`, `uninsdeletesectionifempty`  
**Common params:** `Components`, `Tasks`, `Languages`, `Check`, `BeforeInstall`, `AfterInstall`,
`MinVersion`, `OnlyBelowVersion`

### `[Messages]` section ✅

Overrides installer message strings from `Default.isl`.  
**Format:** `MessageID=Text` or `lang.MessageID=Text`.  
**Implemented:** directive section in `is-spec.yaml` with `internationalization: true` and the full
standard set of `Default.isl` message identifiers (~273) as known keys. Key completion offers a
language-prefix list (flag icon + language name, from the file's `[Languages]` or the built-in
languages) followed by the known message identifiers; after a `lang.` prefix only the message
identifiers are completed. See `IsSectionMessagesKeyProvider`.

### `[CustomMessages]` section ✅

Defines custom localizable strings usable via the `{cm:…}` constant.  
**Format:** Same as `[Messages]` — user-chosen key/value (no predefined names).  
**Implemented:** directive section in `is-spec.yaml` with `internationalization: true` and an empty
attribute list (`internationalization` flag in the spec/schema/`IsSectionDefSpec`). The same
language-prefix completion applies. Each declaration is a renamable named element
(`IsSectionDirectiveEntryMixinImpl`); `{cm:MessageName}` resolves to it (`IsSectionCustomMessageReference`,
red-highlighted when unresolved), offers completion of declared names
(`IsSectionCustomMessageAfterCmProvider`), supports Find Usages, and Rename updates the declaration(s)
— including other-language variants — and all `{cm:}` usages
(`IsSectionCustomMessageReferencesSearcher`).

### `[LangOptions]` section ✅

Language-specific display settings (used inside `.isl` files and overridable in scripts). Implemented
as a directive section in `is-spec.yaml`.  
**Attributes:** `LanguageName`, `LanguageID`, `LanguageCodePage`, `DialogFontName`, `DialogFontSize`,
`DialogFontBaseScaleWidth`, `DialogFontBaseScaleHeight`, `WelcomeFontName`, `WelcomeFontSize`, `RightToLeft`  
**Removed in 6.4:** `TitleFontName`, `TitleFontSize`, `CopyrightFontName`, `CopyrightFontSize` (marked `until: "6.4"`)  
**Extras:** all Windows locales live in `isl-code.yaml` (loaded by `IsLanguageDataService`); each
carries an English name, a flag (reused or generated) and a `builtin` flag marking the Inno-bundled
languages (which also provide `issName` + `messagesFile`). `LanguageID` completion lists every locale
(name + flag + greyed `$hex` id), sorting built-in languages to the top with an "Inno built-in" marker;
a value that is neither `0` nor a listed LCID is flagged as a warning. The `[Languages]` Name/MessagesFile
completion is fed from the `builtin` languages. The Windows `LanguageID` is the single source of truth
for a language's flag and English name: `the `languageId` resolver (`language/file_type/script/PsiUtils`)` resolves a
`[Languages] MessagesFile`
(bundled `compiler:` files from the Inno Setup directory, custom files from SourceDir) and reads its
`[LangOptions] LanguageID`, falling back to the bundled `messagesFile → LCID` mapping when the file is
unavailable. Inlays show flag + English name before `[Languages] MessagesFile`, and the referenced
language's flag before a `lang.` message key. See `IsSectionLanguageInlayHintsProvider`. (The former
Name↔MessagesFile consistency warning was removed.)

### `[ISSigKeys]` section ✅

Declares public keys for `.issig` file-signature verification (used with `issigverify` flag in `[Files]`).  
**Implemented:** parameter section in `is-spec.yaml` (`since: "6.5"`).
**Attributes:** `Name` (req), `KeyFile`, `PublicX`, `PublicY`, `KeyID`, `Group`, `RuntimeID`

---

## Constants (`is-const.yaml`)

Coverage appears **complete** for the constants documented at the time of last check (58 entries including
deprecated). Notable items already marked deprecated/removed in `is-const.yaml`:

- `{hwnd}` — removed in Inno Setup 6.4 ✅ annotated
- `{pf}`, `{pf32}`, `{pf64}`, `{cf}`, `{cf32}`, `{cf64}`, `{fonts}`, `{sendto}` — deprecated ✅ annotated

---

## ISPP Preprocessor (`is-preprocessor.yaml`)

**Spec coverage** is **complete**: 24 directives, 13+ predefined variables and the **full** ISPP built-in
function set (~104 functions from the official `topic_funcs` index, each with `signature`, `return_type`,
`pure` and `description` in `is-preprocessor.yaml`; the `signature` string is parsed into a structured
parameter model at runtime, see **Built-in call validation** below). All standard directives (`#define`, `#undef`,
`#if`/`#elif`/`#else`/`#endif`, `#ifdef`, `#ifndef`, `#include`, `#for`, `#sub`, `#endsub`, `#emit`,
`#expr`, `#pragma`, `#error`, etc.) are present. **Tooling coverage** has a handful of known gaps, listed
under [Known gaps](#known-gaps-ispp-audit-2026-08-01) below.

**`#define` expression analysis & operator highlighting:** The value of a `#define` (and a function-like
macro body) is parsed as a C/C++-like ISPP expression (`…/preprocessor/expression/`: tokenizer, fault-
tolerant precedence-climbing parser, type inference and a recursive name resolver) and validated by
`IsPreprocessorAnnotator`. Missing operators, unbalanced parentheses and **type violations** (`"a" * "b"`,
`1 + "s"`, `"a" < 1`, …) are reported as **errors** anchored at the precise offending token; references to
other `#define`s are resolved **recursively by name** (declaration-order rule + cycle guard) so a conflict
like `#define A "x"` / `#define B 5` / `#define C A * B` is detected on `C`. An operand whose type cannot be
determined statically (unresolved reference, macro parameter, unknown function result, `{…}` constant) is
treated as `any` and suppresses type errors to avoid false positives. The same pass highlights all operator
tokens (`IsPreprocessorSyntaxHighlighting.OPERATOR`). The built-in functions' `return_type` feeds the type
inference (e.g. `Str`→`str`, `Int`→`int`).

**Built-in call validation:** the `signature` string of every built-in is parsed once into a structured
parameter model (`IsPreprocessorBuiltinSignature`, cached by `IsPreprocessorService.builtinSignatures`).
A call is then checked for the number of arguments (parameters with a default value are optional), the type
of every value argument, by-reference parameters (`str*`/`int*`, which require a bare macro name) and the
use of a `void` result as a value. Parameters declared as `Ident`/`Array` (`Defined`, `TypeOf`, `DimOf`)
receive a *symbol name* rather than a value, so the argument must be a bare identifier and is never
evaluated. The two kinds differ deliberately: an `Ident` argument may be undefined and is therefore
excluded from the unresolved-reference check (`symbolArgumentNodes`), while an `Array` argument stays a
resolvable reference (so rename / find-usages keep working) and merely needs no index. A call to a name
that is neither a built-in nor a macro of the file is reported as `Unknown preprocessor function` by the
annotator — deliberately not by the inference, which would duplicate the reference diagnostic.

**Macro parameter model:** the parameter list of a function-like `#define` is parsed by
`parseMacroParameters` (`expression/IsPreprocessorMacroSignature.kt`) into `IsPreprocessorMacroParameter`
(name, declared type `any`/`int`/`str`/`func`, by-reference `*`, default expression, offset). `macroSignatureOf`
maps it onto the same `IsPreprocessorBuiltinSignature` model the built-ins use, so a macro call runs through
the identical checks — argument count between the mandatory and the declared count, argument types against the
declared type (probing the body only where none is declared, `probableMacroParameterTypes`), and a `*`
parameter requiring a bare macro name. An omitted optional argument falls back to its default when the macro's
value is computed (`IsPreprocessorExprValueResolver.callValue`). Inside the body the parameters are in scope:
`IsPreprocessorAnnotator.annotateExpression` binds them to their type, `IsPreprocessorDefineExpressionProvider`
offers them, and each use is an `IsPreprocessorMacroParameterReference` resolving to an
`IsPreprocessorMacroParameterElement` — a declaration element scoped to that one directive, so rename and Find
Usages never reach the macro name.

**Externally contributed symbols:** the ISCC `/D<name>[=<value>]` arguments of the selected run
configuration's build configuration are collected through `IsPreprocessorSymbolProvider.collect` and cached per
script by `IsPreprocessorExternalSymbols` against `IsPreprocessorSymbolTracker`. They are treated as defined by
the branch analysis, by the `{#…}` validation (`IsSectionAnnotator`), by the unresolved-reference check
(`IsPreprocessorAnnotator`), by the type resolver (numeric value → `int`, otherwise `any`) and by the
completion providers. Since nothing in the script declares them, they resolve to no PSI element.

Directive keywords are validated against this spec by `IsPreprocessorAnnotator`: a `#…` directive
whose keyword is not declared in `is-preprocessor.yaml` is flagged as an error (`Unknown preprocessor
directive`, case-insensitive), mirroring the unknown-section/flag/constant checks.

**`#pragma` sub-command analysis:** The `#pragma` sub-commands are modelled as data in
`is-preprocessor.yaml` (`pragma_sub_commands`: `name`, `syntax`, `description`, `argument`, optional
`flag_letters`). `IsPreprocessorAnnotator.annotatePragma` validates the sub-command name (unknown → error)
and its argument by kind: `option`/`parseroption` flag lists are checked against the declared `-<letter>(±)`
form and the allowed letters; `verboselevel` is an integer expression (range 0–10); `message`, `warning`,
`error`, `include`, `inlinestart`, `inlineend`, `spansymbol` are string expressions. Expression arguments
go through the same tokenizer/parser/type-resolver as `#define`, so identifiers inside them resolve to
`#define`s (reference resolution, Find Usages, rename) and an unknown name is an unresolved-reference error.
Completion offers the sub-commands after `#pragma ` and the option flags after `#pragma option `/
`#pragma parseroption ` (`IsPreprocessorPragmaProvider`).

### Per-directive implementation overview

Legend: ✅ implemented · ⚠️ partial · — not applicable.

| Directive                                 | Parsing |                                         Validation                                         |                   Completion                    |                         References                          | MkDocs page       |
|-------------------------------------------|:-------:|:------------------------------------------------------------------------------------------:|:-----------------------------------------------:|:-----------------------------------------------------------:|-------------------|
| `#define`                                 |    ✅    |                             ✅ expr + type + unused + scope kw                              |      ✅ keyword + scope kw + names + funcs       |                              ✅                              | `define.md`       |
| `#undef`                                  |    ✅    |                          ✅ keyword + scope kw + no-match warning                           |       ✅ keyword + scope kw + define names       |                        ✅ → `#define`                        | `undef.md`        |
| `#dim` / `#redim`                         |    ✅    |        ✅ name + size/index type + bounds + scope kw + inline init + redim-needs-dim        | ✅ keyword + scope kw + array names (redim/expr) | ✅ `#dim` ↔ `arr[i]` / `#redim` / `#define arr[i]` / `DimOf` | `arrays.md`       |
| `#include` / `#file`                      |    ✅    |                                 ✅ path + effective script                                  |              ✅ keyword + file path              |                           ✅ file                            | `include.md`      |
| `#emit` / `#expr` / `#insert` / `#append` |    ✅    |                                         ✅ keyword                                          |                    ✅ keyword                    |                              —                              | `output.md`       |
| `#if` / `#elif`                           |    ✅    |                        ✅ expr + type + structure + boolean-literal                         |                    ✅ keyword                    |                        ✅ → `#define`                        | `conditionals.md` |
| `#else` / `#endif`                        |    ✅    |                           ✅ keyword + structure (opener↔#endif)                            |                    ✅ keyword                    |                              —                              | `conditionals.md` |
| `#ifdef` / `#ifndef`                      |    ✅    |                         ✅ name + structure (no error if undefined)                         |                ✅ keyword + name                 |                        ✅ → `#define`                        | `conditionals.md` |
| `#ifexist` / `#ifnexist`                  |    ✅    |                    ✅ string expr + type + structure + file existence                     |                ✅ keyword + expr                 |                    ✅ → `#define` + file                     | `conditionals.md` |
| `#for`                                    |    ✅    | ✅ structure (`{init;cond;incr} body`) + loop var required + cond int-type + slot expr/type |        ✅ keyword + loop var + names/subs        |        ✅ loop var (local) + `#define`/`#dim`/`#sub`         | `for.md`          |
| `#sub` / `#endsub`                        |    ✅    |                           ✅ name + structure (`#sub`↔`#endsub`)                            |              ✅ keyword + sub names              |                 ✅ name ↔ `#for` body / expr                 | `sub.md`          |
| `#pragma`                                 |    ✅    |                             ✅ sub-command + flags + expr/type                              |         ✅ keyword + sub-command + flags         |                       ✅ in expr args                        | `pragma.md`       |
| `#error`                                  |    ✅    |                                         ✅ keyword                                          |                    ✅ keyword                    |                              —                              | `error.md`        |

**Predefined variables via `{#…}`:** Inline emission `{#expr}` (short for `{#emit expr}`) replaces
itself with the value of an expression, so the **value-bearing** predefined variables (`type` `int`/`str`
in `is-preprocessor.yaml` — e.g. `{#__LINE__}`, `{#SourcePath}`, `{#Ver}`) are valid there alongside user
`#define`s. The **valueless `void` symbols** (`__WIN32__`, `ISPP_INVOKED`, `ISCC_INVOKED`, `WINDOWS`,
`UNICODE`) carry no value and are only defined for conditional compilation (`#ifdef` / `#if defined(...)`);
they are therefore **excluded** from `{#…}` everywhere. The single source for the emittable set is
`IsPreprocessorService.emittableVariables`, used by completion (after `{` and after `{#`), the
`{#…}` constant validation in `IsSectionAnnotator`, and the `[Languages] MessagesFile` path
interpretation (`IsMessagesFileResolver`, which expands the path-relevant `{#SourcePath}`/`{#__DIR__}`/
`{#CompilerPath}`/`{#SysPath}`; dynamic/non-path variables stay unresolvable rather than producing a
false error). `#ifdef`/`#if` handling for the `void` symbols is not yet implemented.

**Quick documentation (in-editor):** `IsPreprocessorDocumentationProvider` (registered for language
`ISPP`) provides Quick Doc inside the injected preprocessor fragment for two cases: the **directive
keyword** (`#define`, `#include`, …) shows the directive's description, syntax, `deprecated` marker and
`since`/`until` version range from `is-preprocessor.yaml`; a **predefined variable** referenced in an
expression (e.g. `PREPROCVER`, `__LINE__`) shows its type, description and version range. The
`deprecated` marker is evaluated against the host file's spec target (resolved via
`InjectedLanguageManager`). A **built-in function** shows its description, result type and signature plus
the individual parameters (declared type, `*` by-reference, default value, `Ident`/`Array` symbol
parameters) and, for `pure: false`, a note that it has compile-time side effects. Within a `#define`
expression, completion offers earlier `#define` names **and** the predefined variables
(`IsPreprocessorDefineExpressionProvider`).

**User documentation:** The MkDocs site has a dedicated **Inno Setup Preprocessor** rubric
(`docs/docs/preprocessor/`, all four locales), modelled on the official ISPP docs: an `overview.md`
(general preprocessor description + supported-directive table + inline `{#…}`) plus **one page per
directive** — `define.md`, `undef.md`, `arrays.md` (`#dim`/`#redim`), `include.md` (`#include`/`#file`),
`output.md` (`#emit`/`#expr`/`#insert`/`#append`), `conditionals.md` (`#if`/`#ifdef`/…), `for.md`,
`sub.md` (`#sub`/`#endsub`), `pragma.md` and `error.md`. Keep these pages and the per-directive overview
table above in sync whenever a directive's semantics change.

### Known gaps (ISPP audit 2026-08-01)

Everything the official ISPP docs *declare* is present in `is-preprocessor.yaml`; what is still missing is
tooling on top of that data. Ordered roughly by value/effort ratio:

| # | Gap                                                                                                                                                                                                                                                                             | Status | Where                                                                    |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------|--------------------------------------------------------------------------|
| 1 | ~~Built-in functions are display data only.~~ **Done:** the `signature` string is parsed into a structured parameter model (`parseBuiltinSignature`), so calls are validated for argument count (incl. optional parameters), argument types, by-reference parameters, un-evaluated `Ident`/`Array` parameters and `void` results used as a value. Parameter info and Quick Doc are still missing (see 2–3). | ✅      | `expression/IsPreprocessorBuiltinSignature.kt`, `expression/IsPreprocessorExprTypeInference.kt` |
| 2 | ~~No parameter info (`Ctrl+P`).~~ **Done:** `IsPreprocessorParameterInfoHandler` (EP `codeInsight.parameterInfo`, language `ISPP`) shows the parameter list of the enclosing call and highlights the current argument — built-ins from the parsed signature (types, `*` by-reference, default values), function-like macros with their *probable* parameter/result types. Documentation text inside the popup is not shown yet (see 3). | ✅      | `feature/editor/IsPreprocessorParameterInfoHandler.kt`, `expression/IsPreprocessorCallSite.kt` |
| 3 | ~~Quick Doc covers only directive keywords and predefined variables.~~ **Done:** built-in functions have their own Quick Doc (editor and completion popup) fed from `is-preprocessor.yaml` — description, result type, signature, the parameters broken down from the parsed signature (type, by-reference, default value, `Ident`/`Array` symbol parameters) and a side-effect note for `pure: false`. | ✅      | `IsPreprocessorDocumentationProvider.generateFunctionDoc`                 |
| 4 | ~~Valueless `void` symbols in conditions.~~ **Done:** the branch analysis seeds every predefined symbol as *defined*, so `#ifdef ISPP_INVOKED` / `defined(...)` decide correctly while their compiler-decided values stay unknown.                                                 | ✅      | `IsPreprocessorBranchAnalysis` (`seedPredefined`)                        |
| 5 | ~~`#ifexist` / `#ifnexist` never checks the file.~~ **Done:** the file name is resolved through `IsIncludePaths` and decides the branch — a literal path directly, a filename *expression* (e.g. `#ifexist MyFile`) after evaluating it against the current symbol table; only a name that is not statically computable stays undecidable.                                                                                                     | ✅      | `IsPreprocessorBranchAnalysis` (`evaluateBranchCondition`)               |
| 6 | ~~No line continuation with a trailing `\`.~~ **Done:** the host lexers keep a `#…` line continued with a trailing backslash in one `HASH_LINE` token, so ISPP is injected over all of its physical lines; the ISPP lexer and the expression tokenizer report the continuation as whitespace. | ✅      | `parsing/IsSectionLexer.flex`, `parsing/IsTemplateLexer.flex`, `parsing/IsPreprocessorLexer.flex`, `expression/IsPreprocessorExprTokenizer.kt` |
| 7 | ~~No conditional compilation in the editor.~~ **Done:** `#if` branches are evaluated three-valued (active/inactive/undecidable); provably dead branches are dimmed and stop reporting problems, undecidable conditions get a blue-underline marker, and the effective script prunes accordingly. The computed `#define` values the analysis produces are surfaced in Quick Doc *and*, since 2026-08-04, as an end-of-line inlay hint. | ✅      | `IsPreprocessorBranchAnalysis`, `IsEffectiveScriptBranches`, `IsDefineValueInlayHintsProvider` |
| 8 | ~~Function-like macro parameters are only partially modelled.~~ **Done:** the ISPP declaration `[<type>] [*]<name> [= <default>]` is parsed (`parseMacroParameters`) into `IsPreprocessorMacroParameter` and mapped onto the shared signature model, so a call is validated like a built-in call (min/max arity via defaults, declared argument types, by-reference arguments). Inside the body the parameters are in scope: typed for the validation, offered in completion, and resolvable/renameable via `IsPreprocessorMacroParameterReference` → `IsPreprocessorMacroParameterElement`. Declared types, `*` and defaults are shown in parameter info and Quick Doc. | ✅      | `expression/IsPreprocessorMacroSignature.kt`, `expression/IsPreprocessorExprTypeResolver`, `expression/IsPreprocessorExprTypeInference`, `feature/reference/IsPreprocessorMacroParameterReference`, `completion/provider/IsPreprocessorDefineExpressionProvider` |
| 10 | **Collecting the ISPP directives of a file is quadratic in file size.** `preprocessorDirectivesWithHostLine` / `isppDirectivesWithHostOffset` call `InjectedLanguageManager.enumerate` once per preprocessor line, and that call is not constant in file size — measured 12–13x for a 4x larger script; ~1500 conditional blocks take ~25 s. Predates the branch analysis (the annotator, folding and sticky lines collect the same way), but the branch analysis is the first consumer running it over the whole file per document revision. Irrelevant at realistic script sizes. Guarded by the disabled assertions in `IsPreprocessorBranchAnalysisPerformanceIT`. | ❌      | `IsPreprocessorConditionalStructure`, `language/parser/PsiUtils.kt`       |
| 9 | ~~ISCC command-line symbols (`/D…`) are only considered for branch analysis.~~ **Done:** the `/D` symbols are collected once per script (`IsPreprocessorExternalSymbols`, cached against `IsPreprocessorSymbolTracker`) and count as defined everywhere — `{#Name}` emission, identifiers in `#define`/`#if` expressions (numeric value → `int`) and both completion lists. They have no declaration in the script, so they stay without a navigation target. | ✅      | `IsPreprocessorExternalSymbols`, `IsPreprocessorSymbolProvider`, `IsRunConfigurationSymbolProvider`, `IsBuildConfiguration` |

Items 1–9 are done: the `signature` string is machine-readable (`parseBuiltinSignature`) and drives
the call validation, the parameter info popup and the per-parameter section of the built-in Quick Doc; a
directive spread over several physical lines is parsed as one directive; a function-like macro's parameter
list is parsed the same way its calls are validated; the `/D` symbols of the selected build configuration
count as defined everywhere, not only in a condition; and the computed value of a `#define` is surfaced both
in Quick Doc and as an inlay hint. Item 10 is the only one left — a performance note that is irrelevant at
realistic script sizes.

---

## IDE Features

| Feature                                         | Status | Notes                                                                                                                                                                                                                   |
|-------------------------------------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Syntax highlighting (Section)                   | ✅      |                                                                                                                                                                                                                         |
| Syntax highlighting (ISPP)                      | ✅      |                                                                                                                                                                                                                         |
| Code completion — sections                      | ✅      |                                                                                                                                                                                                                         |
| Code completion — attributes                    | ✅      |                                                                                                                                                                                                                         |
| Code completion — flags                         | ✅      |                                                                                                                                                                                                                         |
| Code completion — constants                     | ✅      |                                                                                                                                                                                                                         |
| Code completion — ISPP directives               | ✅      |                                                                                                                                                                                                                         |
| Code completion — `{#…}` ISPP variables         | ✅      | After `{` and `{#`: user `#define`s + value-bearing predefined variables + ISCC `/D` symbols of the build configuration; valueless `void` symbols excluded                                                                                                             |
| Code completion — `[Languages]`                 | ✅      | Built-in names + MessagesFile, with flag icons                                                                                                                                                                          |
| Code completion — `LanguageID`                  | ✅      | Windows LCIDs: name + flag + greyed `$hex` id                                                                                                                                                                           |
| Code completion — message i18n prefix           | ✅      | `[Messages]`/`[CustomMessages]`: `lang.` prefix list (declared `[Languages]` only; flag + name from LanguageID) + message ids                                                                                           |
| Code completion — `{cm:…}`                      | ✅      | Declared custom-message names                                                                                                                                                                                           |
| `{cm:…}` reference / find usages / rename       | ✅      | Resolves to `[CustomMessages]`; red when unresolved; rename keeps language variants in sync; `cm` rendered italic                                                                                                       |
| `lang.` prefix reference / find usages / rename | ✅      | Prefix resolves to a `[Languages] Name`; red when undeclared; renaming the Name updates all prefixes                                                                                                                    |
| Inlay hints — computed `#define` value           | ✅      | `IsDefineValueInlayHintsProvider` (declarative API, EP `codeInsight.declarativeInlayProvider`): end-of-line hint with the statically computed value of a `#define` assembled from macros and operators, calls to user-defined function-like macros included. Excluded: plain literals (the value already stands in the source), function-like macros themselves, array element defines, value-less defines and expressions that cannot be evaluated — among them every **built-in** call, since `IsPreprocessorExprValueResolver` validates built-ins but never executes them. Toggle under *Editor \| Inlay Hints* |
| Inlay hints — language flags                    | ✅      | Flag + English name (from the LanguageID in the referenced `.isl`) before `[Languages] MessagesFile` and `[LangOptions] LanguageID`; flag of the referenced language before a `lang.` message key                       |
| Brace matching `[]`, `{}`, `()`                 | ✅      |                                                                                                                                                                                                                         |
| Code folding                                    | ✅      | Sections, multi-pair entries, and `#if … #endif` / `#sub … #endsub` blocks (when wholly inside one section or wholly outside)                                                                                           |
| Structure view                                  | ✅      |                                                                                                                                                                                                                         |
| Documentation popup (Section)                   | ✅      | Sections/attributes/flags/constants from spec YAML (`IsSectionDocumentationProvider`): description, type, `required`/`deprecated` markers, `since`/`until` version section                                              |
| Documentation popup (ISPP)                      | ✅      | `IsPreprocessorDocumentationProvider` (lang `ISPP`): directive keyword (`#define`/`#include`/…) → description + syntax + `deprecated` + `since`/`until`; predefined variable → type + description + `since`/`until`; built-in function → description + result type + signature + per-parameter details + side-effect note (`pure: false`); own `#define`/macro → inferred type, computed value resp. parameter/return types |
| Parameter info (ISPP functions)                 | ✅      | `IsPreprocessorParameterInfoHandler` (`codeInsight.parameterInfo`, lang `ISPP`): parameter list of the enclosing built-in / function-like macro call with the current argument highlighted, plus the result type where determinable. Arguments are additionally **validated** against the parsed signature (count, types, by-reference, `Ident`/`Array`, `void` result) |
| Conditional compilation (`#if` branches)        | ✅      | Three-valued: provably dead branches are dimmed (`ISS_INACTIVE_BRANCH`) and produce no diagnostics; an undecidable condition keeps both branches and gets a blue-underline marker (`ISS_UNDECIDABLE_CONDITION`). Integer *and* string comparisons (case-insensitive, as in ISPP) are evaluated; `#ifexist`/`#ifnexist` (literal or computed file name) and run-configuration `/D` symbols are taken into account; the effective script prunes the same way (collapsing the blank lines the removed branches leave behind) |
| ISPP line continuation (trailing `\`)           | ✅      | A `#…` line ending with `\` continues on the next line: the host lexers keep it in one `HASH_LINE` token, the ISPP lexer and the expression tokenizer treat the continuation as whitespace; the formatter treats it as a line end and normalises the space before `\` plus the indent of the continued lines |
| Find usages                                     | ✅      | For ISPP `#define` references (incl. `#undef` usages) and for macro parameters inside their own macro                                                                                                                                                                   |
| Rename refactoring                              | ✅      | For ISPP identifiers, incl. function-like macro parameters (scoped to their `#define`)                                                                                                                                                                                                    |
| Commenter (`Ctrl+/`)                            | ✅      |                                                                                                                                                                                                                         |
| Quote handler                                   | ✅      |                                                                                                                                                                                                                         |
| ISPP language injection                         | ✅      | Preprocessor lines injected into ISI                                                                                                                                                                                    |
| Semantic annotations / errors                   | ✅      |                                                                                                                                                                                                                         |
| ISPP directive validation                       | ✅      | Unknown `#…` directive keywords (not in `is-preprocessor.yaml`) flagged as errors, case-insensitive (`IsPreprocessorAnnotator`)                                                                                               |
| `{#…}` variable validation                      | ✅      | `{#name}` accepted for user `#define`s, value-bearing predefined variables and ISCC `/D` symbols; valueless `void` symbols stay flagged as invalid emissions                                                                                 |
| `{#…}` in `MessagesFile` path resolution        | ✅      | `IsMessagesFileResolver` expands path-relevant predefined variables (`{#SourcePath}`/`{#__DIR__}`/`{#CompilerPath}`/`{#SysPath}`); dynamic ones stay unresolvable (no false error)                                      |
| Completion — deprecated members struck through  | ✅      | Deprecated sections/attributes/flags/constants/message-keys are rendered with strikethrough in the lookup (`withStrikeoutness`, target-aware); no extra "deprecated" tail text — it follows from the strikethrough      |
| [Code] section Pascal support                   | ❌      | No Pascal intellisense; treated as plain text                                                                                                                                                                           |
