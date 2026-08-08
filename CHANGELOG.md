<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Inno-setup Changelog

## [Unreleased]

### Added

- **A section announces its entry syntax.** The section-name completion now carries the matching icon on
  every offered section, and the same icon is rendered as an inlay hint directly behind the `[` of a section
  header: `=` for a directive section such as `[Setup]`, `:` for a parameter section such as `[Files]`, and
  the script icon for `[Code]`. The syntax of a section is therefore visible before its first entry exists.
  The hint can be switched off separately under *Inlay Hints* as "Section type".

### Changed

- **Sections and their entries have their own icons.** The structure view, breadcrumbs and navigation bar no
  longer borrow the generic Java class and field icons. A section carries the Inno Setup tile with a `[ ]`
  bracket glyph; an entry carries its separator, so the two entry kinds are told apart at a glance: `=` for a
  directive entry of a section such as `[Setup]`, `:` for a parameter entry of a section such as `[Files]`.

### Fixed

- **No more misplaced editor assistance inside `[Code]`.** The section holds free-form Pascal Script, yet
  quick documentation popped up on ordinary identifiers, `"` was auto-closed although Pascal strings use
  `'`, the "Flip parameters" intention appeared at every statement-terminating `;`, and cross-section
  references were resolved from Pascal code. All of this is switched off in `[Code]` now. The preprocessor
  stays available — `#…` directive lines and inline `{#…}` emissions keep their documentation and
  references, because Inno Setup evaluates ISPP inside `[Code]` as well.

- **Building and running a script works again.** The output directory was passed to ISCC with an extra pair of
  quotes, which the process launcher escaped into literal characters — the compiler received an unusable `/O`
  argument and every build failed, regardless of the script. Building now succeeds for all official Inno Setup
  example scripts, which are compiled end-to-end by the test suite from now on.

- **`[Code]` sections no longer produce syntax errors.** Their content is free-form Pascal Script and is now
  handed to the parser as opaque lines instead of being read as `Key=Value` / `Key: Value` entries. Until now
  the first Pascal construct ended the section, which scattered error markers over the rest of the script and
  could make entries of the following sections be misjudged.
- Paths starting with a compile-time prefix (`userdocs:`, `compiler:`) are no longer reported as containing
  an invalid character. `OutputDir=userdocs:Inno Setup Examples Output` — the value used by the official
  Inno Setup example scripts — is valid again.
- `{code:FunctionName}` and `{code:FunctionName|Parameter}` are recognised as constants instead of being
  reported as unknown.
- ISPP assignments (`=`) inside an expression are understood, and the predefined `Local` array is known. A
  macro such as `#define M(str S) Local[0] = f(S), g(Local[0])` no longer reports errors.
- `AppVersion` is no longer reported as missing when `AppVerName` is present — Inno Setup accepts either one.
- The `UsePreviousLanguage` rule no longer fires on an ISPP emission (`AppId={#MyAppId}`): the preprocessor
  replaces it with literal text before the script is compiled, so no constant remains.
- An entry may be continued on the next line with a trailing `\` (as the `[ISSigKeys]` entries do).
- An unquoted parameter value may contain a colon (`Source: compiler:WizClassicSmallImage.bmp`).
- Constants may be nested inside a quoted string (`"{cm:UninstallProgram,{cm:MyAppName}}"`), and an inline
  emission may carry a whole expression (`{#file AddBackslash(SourcePath) + "License.txt"}`).
- **A missing source file or directory is now a warning instead of an error**, and it is not reported at all
  when a flag of the entry makes the file optional (`external`, `skipifsourcedoesntexist`). Likewise
  `DestDir` is no longer demanded for an entry flagged `dontcopy`. Which flags waive a requirement is part of
  the specification, not hard-coded.
- Numbers may group their digits with underscores (`ExtraDiskSpaceRequired=7_000_000`).
- An inline emission carrying a preprocessor directive (`{#file …}`, `{#emit …}`) is recognised as such and
  validated as that directive, instead of being reported as an unknown constant. A directive that produces
  no value is reported when used inside `{#…}`.
- `{cm:…}` is no longer reported as unknown when the script declares a `MessagesFile` whose custom messages
  cannot be read because no Inno Setup installation path is configured.
- **Show Effective Script** no longer repeats parts of the script. A condition that could not be resolved
  left the copy position behind, so everything before its marker was emitted again for every further marker.

## [0.7.0]

### Added

- **Computed `#define` values are shown as an inlay hint** at the end of the directive line, so a macro
  assembled from other macros and operators (e.g. `#define MyExe MyName + ".exe"`) shows its result without
  compiling the script. Calls to your own function-like macros are evaluated as well. Plain literals stay
  unannotated — their value already stands in the source — as do function-like macros themselves, array
  element defines and expressions that cannot be evaluated at analysis time, including calls to ISPP built-in
  functions. The hint can be switched off under **Settings | Editor | Inlay Hints**.
- **Conditional compilation**: `#if`, `#elif`, `#else`, `#ifdef`, `#ifndef`, `#ifexist` and `#ifnexist` are
  now evaluated in the editor. A branch that provably is not compiled is dimmed and no longer reports
  errors or warnings.
- If a condition cannot be evaluated with certainty — because it depends on the environment, a file being
  read, an external program, or a symbol only defined on the compiler command line — **both branches stay
  visible** and the condition is marked with a blue wavy underline and a hint explaining why it could not be
  resolved. Nothing is ever dimmed on a guess.
- Conditions comparing strings (e.g. `#if MyDefine == "test"`) are now evaluated as well — case-insensitively,
  like the Inno Setup preprocessor itself — instead of being reported as unresolvable.
- `#ifexist` and `#ifnexist` now actually check whether the file exists and decide their branch — both for a
  literal path and for a file name computed from `#define`s.
- **Show Effective Script** resolves conditionals as well: a decided `#if` is replaced by the content of its
  live branch, while an undecidable one is kept in full with a comment noting that it could not be resolved.
  Blank lines left behind by the removed branches are cleaned up — at most one blank line remains where a
  block was pruned, and none at the beginning or end of the script.
- **Build configurations**: named sets of compile options — preprocessor symbols passed to the compiler as
  `/D` (e.g. `DEBUG` or `VERSION=2`), an output directory override and additional `ISCC` options — managed
  under **Settings | Inno Setup | Build**. A run configuration only refers to one by name, so editing the
  options takes effect in every run that uses them. Each configuration is stored as a single file in the
  project's `.build` directory and can be shared with the team through version control.
- A project starts with two configurations: **Release** (no symbols) and **Debug** (`DEBUG`); every new run
  configuration starts on **Debug**.
- The **gutter icon** next to `[Setup]` now opens a list of the available build configurations, and the
  context menu entries **Run Script** / **Build Script** became submenus listing them.
- The editor resolves `#ifdef` branches with the symbols of the **selected** run configuration's build
  configuration, so what is dimmed matches what the next run builds. Without a selected Inno Setup run
  configuration a banner above the script says so and offers a drop-down to pick one.
- A build is now only reused when the **content of the build configuration** is unchanged as well —
  switching from `Debug` to `Release`, or editing `Debug`'s symbols, forces a rebuild; renaming a
  configuration does not.
- **Built-in preprocessor functions are now checked against their signature**: a wrong number of arguments
  (respecting optional parameters such as `Find(…, Index = 1)`), an argument of the wrong type
  (e.g. a string where `Copy` expects an integer index), and a by-reference parameter (`str*` / `int*`) that
  does not receive a macro name are reported as errors at the offending argument.
- Calling a built-in that returns nothing (`Error`, `Warning`, `Message`, `FileClose`, …) as a **value**
  inside an expression is now reported.
- Calling a name that is neither a built-in nor a macro of the script is now reported as
  **Unknown preprocessor function** instead of a generic unresolved reference.
- **Quick documentation for built-in preprocessor functions** now lists the parameters individually — the
  declared type, whether a parameter is passed by reference, its default value, and whether it takes a
  symbol name instead of a value — and marks functions that have compile-time side effects (file system,
  registry, environment, external programs).
- **Parameter info (`Ctrl+P`) for preprocessor calls**: while the caret is inside an argument list, the
  parameter list of the called function is shown and the argument being typed is highlighted. Built-in
  functions show their declared parameter types, by-reference markers and default values; function-like
  macros of the script show the parameter and result types inferred from their body wherever those can be
  determined.
- **Line continuation in preprocessor directives**: a `#…` line ending with a backslash is continued on the
  following line, exactly as the Inno Setup preprocessor does it. Such a directive is now parsed, highlighted,
  validated and completed as a single directive instead of breaking parsing at the line break. Trailing spaces
  and tabs behind the backslash are tolerated and removed by the formatter.
- **Macro parameters are now fully modelled**: the ISPP declaration `[<type>] [*]<name> [= <default>]`
  (`#define Multiply(int A, int B = 10) A * B`) is parsed, so a call is validated like a built-in call —
  argument count between the mandatory and the declared parameter count, argument types against the declared
  ones, and a by-reference parameter (`*`) that does not receive a macro name. An omitted optional argument
  falls back to its default when the macro's value is computed.
- Inside a macro body its **parameters are in scope**: they are offered in completion, resolve to their
  declaration in the parameter list (go-to-definition, Find Usages) and can be renamed with **Shift+F6**
  throughout the macro — without ever touching the macro name. A parameter with a declared type is
  type-checked in the body instead of being treated as `any`.
- Declared parameter types, `*` and default values are shown in **parameter info** (`Ctrl+P`) and in
  **Quick Documentation** of a macro.
- **Preprocessor symbols from the command line (`/D`)** now count as defined everywhere, not only for
  conditional compilation: `{#Name}` emits such a symbol without an "unknown constant" error, an identifier
  in a `#define`/`#if` expression resolves to it, and both completion lists offer it. A numeric value
  (`/DVERSION=2`) makes the symbol an integer for the type check. Switching the selected run configuration or
  editing its build configuration switches the symbols immediately.
- **Reformat Code handles line continuations**: a continuation is treated like a line end, so no spacing rule
  reaches across it and a multi-line directive keeps its layout. Two new options under **Code Style | Inno
  Setup | Spacing** normalise the space before the trailing `\` and indent the continued lines with the
  language's continuation indent.

### Changed

- The run configuration's **Preprocessor symbols** field has been replaced by a **Build configuration**
  drop-down. Existing run configurations are migrated automatically: their symbols move into a build
  configuration, reusing a matching one where possible.
- New colour scheme entry **Preprocessor | Inactive branch** for the dimming (Settings | Editor | Color
  Scheme).
- The build configurations under **Settings | Inno Setup | Build** are now managed like code style schemes:
  the four buttons were replaced by a gear button next to the drop-down offering **Add…**, **Duplicate…**,
  **Rename…** and **Delete**.

### Fixed

- The argument of `Defined(...)` and `TypeOf(...)` is no longer reported as an unresolved reference — these
  built-ins take the *name* of a symbol, which may legitimately be undefined.
- `DimOf(MyArray)` no longer demands an index: the array is passed as a whole.
- Changing the build configuration — switching the selected run configuration, picking another build
  configuration in it, or editing the configurations under **Settings | Inno Setup | Build** — now updates
  the dimmed `#if` branches immediately instead of only after the script is edited.

## [0.6.4]

### Added

- **Sticky lines**: while scrolling, the enclosing section header (`[Setup]`, `[Files]`, …) stays pinned at
  the top of the editor.
- **Breadcrumbs**: the editor now shows the breadcrumbs of the current line (`setup.iss › [Files] › Source`);
  preprocessor blocks are deliberately left out of the breadcrumbs bar.

## [0.6.3]

- **Compatibility**: minimum version of IntelliJ IDEA is now: 2026.2
- **Dependency Updates**

## [0.6.2]

- **Fixes for new IntelliJ IDEA**: fixes to support newer versions of IntelliJ IDEA since 2026.2

## [0.6.1]

### Added

- **Code formatting**: **Reformat Code** (<kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>L</kbd>) now formats `.iss` /
  `.isl` scripts — one space around `=`, `key: value` / `; ` spacing in parameter lines, tight section
  brackets (`[Setup]`), exactly one blank line between sections, and no leading indentation before a key.
  Preprocessor directives are formatted too: one space around the arithmetic operators (`+ - * / %`), tight
  `()` / `[]` and `#for {…}` braces, and `i = 0; i < n; i++`-style spacing (space around `=`, `; ` between
  the parts) in `#for` headers. Each rule is configurable on a dedicated
  **Settings ▸ Editor ▸ Code Style ▸ Inno Setup** page, whose live preview updates as you toggle the options;
  the `[Code]` (Pascal Script) section is left untouched.

## [0.6.0]

### Added

- add deployment for language support libs for custom usage into the GitHub package registry

- **ISPP subroutines (`#sub` / `#endsub`)**: `#sub Name … #endsub` blocks are now fully supported. The
  subroutine name navigates and renames together with its uses (e.g. a `#for` body that calls it) and is
  offered in completion. `#sub … #endsub` blocks fold like `#if … #endif`, and an unterminated `#sub` or a
  stray `#endsub` is flagged as an error.

- **ISPP loops (`#for`)**: `#for {Init; Cond; Incr} Body` is now validated. The loop variable declared in
  the initializer (e.g. `i = 0`) navigates, renames and is offered in completion within the loop's condition,
  increment and body — just like a `#define`/`#dim` symbol — while staying scoped to its loop. The condition,
  increment and the single same-line body expression are checked with the full ISPP expression engine
  (operators, functions, references and result types), the body resolves a referenced `#sub`/`#define`, and
  errors are reported for a missing loop variable, a non-integer condition or an ill-typed expression. The
  `{` and `}` of the loop header are matched as a brace pair.

### Changed

- Internal restructuring: the plugin is now organised into separate Gradle modules (`language:script` and
  `language:preprocessor`, pulled in by the main plugin). This has no effect on plugin behaviour.

## [0.5.2]

### Added

- **ISPP arrays (`#dim` / `#redim`)**: Arrays are now fully supported. Declare them with `#dim Name[Size]`
  (optionally with a `private`/`protected`/`public` scope and an inline `{...}` initialiser), resize with
  `#redim`, assign elements via `#define Name[Index] Value`, and read them as `Name[Index]` in any
  expression; `DimOf(Name)` returns the element count. Array names are completed (after `#redim ` and inside
  expressions), navigate and rename together across the `#dim`, every `Name[Index]` use, `#redim` and
  `DimOf`, and element values are statically evaluated across `#dim`/`#define`. Errors are reported for
  indexing a non-array, using an array without an index, a non-integer index or size, a `#redim` without a
  matching `#dim`, a mismatched inline-initialiser count, and statically out-of-bounds indices. The `[` and
  `]` brackets are matched as a brace pair.

- **File & directory path validation**: File- and directory-valued parameters across all sections are now
  validated. Two cases are distinguished:
    - **Must exist at compile time** (build-machine source paths, e.g. `[Setup]` `SetupIconFile`,
      `LicenseFile`, `InfoBeforeFile`/`InfoAfterFile`, the wizard image/style files, `SourceDir`,
      `SignedUninstallerDir`, `[Files]` `Source`, and the `[Languages]` override files): a path that does
      not exist, or that points to the wrong kind (a directory where a file is expected, or vice versa),
      is flagged as an error. Wildcard patterns (`*`, `?`), comma-separated lists, and paths containing
      unresolvable `{…}` constants are left unchecked so no false errors appear.
    - **Need not exist at compile time** (target/runtime paths, e.g. `[Files]` `DestDir`/`DestName`,
      `[Dirs]` `Name`, `[Icons]` `Name`/`Filename`/`WorkingDir`/`IconFilename`, `[Run]`/`[UninstallRun]`
      `Filename`/`WorkingDir`, `[INI]` `Filename`, `[InstallDelete]`/`[UninstallDelete]` `Name`,
      `[Setup]` `DefaultDirName`/`UninstallFilesDir`/`UninstallDisplayIcon`/output paths): the value is
      checked for characters that are invalid in a Windows path (`< > " |`, and a `:` that is not a drive
      specifier such as `C:\…` or a URL scheme).

  The `[Languages]` `MessagesFile` keeps its dedicated handling for `compiler:` paths and ISL content
  validation.

- **`#if` / `#elif` / `#else` / `#endif` conditional support**: The `#if`/`#elif` condition is now
  analysed by the ISPP expression engine — operators are highlighted, syntax and type errors are flagged,
  and identifiers resolve to `#define`s (go-to-definition, Find Usages, rename), with unknown names
  reported as errors. A directly used boolean literal (`true`/`false`/`yes`/`no`) is highlighted yellow
  with a warning, since ISPP has no booleans. The block **structure is validated**: a `#if` (or
  `#ifdef`/`#ifndef`/`#ifexist`/`#ifnexist`) without a matching `#endif`, and a stray
  `#elif`/`#else`/`#endif`, are flagged. A complete `#if … #endif` block can be **folded** when it lies
  entirely within one section or entirely outside any section.
- **`#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist` argument support**: The identifier of an
  `#ifdef`/`#ifndef` now resolves to its `#define` (go-to-definition, Find Usages, rename) and `#define`
  names are offered in completion — an unknown name is intentionally **not** an error here. The filename of
  an `#ifexist`/`#ifnexist` is analysed as an ISPP string expression (type-checked, references to `#define`s
  resolved) with file-name completion inside its `"…"` string.
- **`#undef` directive support**: `#undef Name` now resolves to the matching `#define`, with
  go-to-definition, Find Usages and rename across the `#define`, its `{#Name}` uses and the `#undef`.
  Completion after `#undef ` offers the names of the defines declared earlier. An `#undef` without a
  matching `#define` is grayed out with a quick fix to remove it.
- **`#define`/`#undef` scope keywords**: The optional visibility keywords `public`, `protected` and
  `private` before the macro name (e.g. `#define public MyVar 1`) are now recognized — highlighted like
  keywords and offered in completion — without breaking name resolution or referencing.

- **`#pragma` directive support**: The `#pragma` sub-commands (`option`, `parseroption`, `message`,
  `warning`, `error`, `verboselevel`, `inlinestart`, `inlineend`, `include`, `spansymbol`) are now
  validated — unknown sub-commands, invalid option flags and arguments of the wrong type are flagged as
  errors — and completion offers the sub-commands after `#pragma ` and the option flags after
  `#pragma option `/`#pragma parseroption `. `#define`s used inside a `#pragma` expression argument resolve,
  and support Find Usages and rename.

## [0.5.1]

- Updates for automatic release
- Copyright updates
- Fixes for warnings in the Plugin upload process

## [0.5.0]

### Added

- **`.ist` template files**: Added a new free-text *Inno Setup Template* file type (`.ist`) as an
  alternative `#include` target. Template files are not validated, but support the ISPP preprocessor
  directives, brace matching for `[]`/`()`, and section-name completion after `[`. `#include` completion
  offers `.ist` files, the extract-to-file intention proposes a `.ist` name, and a "New Inno Setup Template"
  action creates one.
- **`#include` directive support**: `#include "file"` paths resolve to the referenced script, with
  go-to-definition (**Ctrl+B** / **Cmd+B**) and path completion as you type.
- **`#include` validation**: A missing or non-existent include file, and a non-literal or empty include
  path, are flagged as errors. Problems detected inside an included file (unknown directives, flags,
  undefined constants, warnings) are surfaced on the `#include` line of the including script, while
  required-section checks correctly account for content contributed by includes.
- **`#include` intentions**: Added an intention to inline an `#include` file's content in place (optionally
  deleting the now-inlined file), and an intention to extract selected lines into a new file and replace them
  with an `#include`.
- **`#include` path refactoring**: Renaming or moving an included file now updates the `#include` path
  automatically.
- **Show effective script**: Added a "Show Effective Script" action that opens the fully
  `#include`-resolved script in a read-only tab.

## [0.4.2]

### Added

- **Inno Setup Run configuration**: Added a run configuration to launch and build Inno Setup scripts directly
  from the IDE, with a dedicated build settings page and context menu actions for run and build.
- **`[INI]` section support**: Added support for the `[INI]` section including parameter completion and
  validation.
- **Preprocessor expression calculation**: Added evaluation of ISPP expressions and `#define` values,
  including fixes for edge cases and missing parameters.
- **Predefined directive variables**: Improved support for `{#...}` inline preprocessor variables with
  better completion and highlighting.
- **Documentation lookup in completion popup**: Added inline documentation display when browsing completion
  suggestions.

### Fixed

- Fixed broken preprocessor syntax highlighting.
- Fixed flag completion suggestions.
- Fixed annotator for `UsePreviousLanguage` usage.
- Fixed custom message reference handling.

## [0.4.1]

### Added

- **Inno Setup language file support**: Added `.isl` file type support reusing the ISS parser and editor
  infrastructure with ISL-specific section validation.
- **ISL file creation**: Added "New Inno Setup Language File" actions and dialogs for filename, language name, and
  Windows language ID.
- **Language-aware sections**: Added support for `[LangOptions]`, `[Messages]`, and `[CustomMessages]` in language
  files and scripts, including file-type-scoped required/deprecated metadata.
- **Custom message references**: Added completion, reference resolution, unresolved-reference highlighting, find usages,
  and rename refactoring for `{cm:MessageName}` references.
- **Language prefix references**: Added completion, navigation, find usages, rename, and unresolved-prefix validation
  for
  localized message keys such as `german.WelcomeLabel1`.
- **Windows language metadata**: Added Windows LCID data, built-in Inno Setup language metadata, language flag icons,
  and
  `LanguageID` completion/validation.
- **Language inlay hints**: Added language flag and English-name inlays for `[Languages] MessagesFile`,
  `[LangOptions] LanguageID`, and localized message prefixes.
- **Built-in language completion**: Added `[Languages]` `Name` and `MessagesFile` completion for bundled Inno Setup
  languages, with quoted and unquoted value support.
- **Quick fixes**: Added quick fixes for missing sections, missing directives, missing parameters, missing required
  flags, redundant flags, unused `#define`s, empty sections, trailing semicolons, and moving `[Code]` to the end.
- **Version-aware specs**: Added `since`/`until` metadata for sections, attributes, flags, directives, and constants,
  and
  introduced annotations based on the configured minimum Inno Setup version.
- **Plugin settings**: Added settings for configuring the local Inno Setup installation path and version-related
  services.
- **Editor actions**: Added an intention to flip semicolon-separated parameter entries and a section mover for script
  sections.
- **Structure/navigation improvements**: Added a structure-aware navigation bar model and expanded section/navigation
  tests.
- **Documentation coverage tracking**: Added `STATUS.md` to track official Inno Setup documentation coverage.
- **Docs site updates**: Added MkDocs pages for script files, language files, `[INI]`, `[ISSigKeys]`, `[LangOptions]`,
  `[Messages]`, and `[CustomMessages]`, and updated navigation and landing-page content.

### Changed

- **Spec model naming**: Renamed spec types and resources from `Iss*` to `Isi*` where they describe shared ISS/ISL
  script syntax.
- **Spec target model**: Changed `required` and `deprecated` metadata to file-type-scoped targets so rules can apply to
  `.iss`, `.isl`, both, or neither.
- **Constant metadata**: Refactored constant categories from `category` to `type`.
- **Setup coverage**: Expanded `[Setup]` spec coverage to all known current parameters.
- **Flag validation**: Improved handling for missing, redundant, conflicting, and version-gated flags.
- **Schema documentation**: Added descriptions to JSON schema definitions for ISI, ISPP, and constants to improve
  generated documentation and tooltips.
- **Language icons**: Replaced/expanded language flag assets as SVGs with copyright headers.
- **Build tasks**: Improved lexer/parser generation tasks and cleanup of stale generated sources.
- **Licence generation**: Optimized licence report handling.

### Fixed

- **ISL preprocessor handling**: Preprocessor directives are now always marked as errors in `.isl` files with the
  message `Preprocessor directives are not allowed in Inno Setup language (.isl) files`.
- **ISL completion**: Disabled ISPP directive suggestions after `#` in `.isl` files.
- **Structure view**: Fixed structure view behavior and expanded regression coverage.
- **Color scheme regression**: Reverted removal of bundled color schemes so explicit theme coloring remains available.
- **Parser/test regressions**: Fixed test failures and added broader parser, PSI tree, completion, annotator,
  navigation,
  quick-fix, and file-action coverage.

## [0.4.0]

### Added

- **ISPP language support**: Full parsing, syntax highlighting, code completion, navigation and injection
  of the Inno Setup Pre-Processor into ISS scripts
- **`#define` constants**: Reference resolution, code completion, highlighting and rename refactoring for simple
  and typed `#define` directives
- **Section cross-references**: References between sections (e.g. `[Files]` → `[Components]`) via mixin-based
  reference handling
- **Color settings page**: Configurable coloring of syntax elements via IDE settings
- **New file action**: Create `.iss` files directly from the "New…" menu
- **Spec-based architecture**: YAML specification files for all Inno Setup sections, attributes, flags, constants
  and ISPP directives as the basis for completion and documentation
- **Inline documentation**: Context-sensitive documentation for all Inno Setup sections and their keys directly in
  the editor
- **Boolean completion**: Automatic completion of boolean values (`yes`/`no`) for directives in the `[Setup]` section

### Changed

- **Embedded constants in strings**: Strings containing `{app}`, `{win}`, `{#Name}` etc. are correctly parsed,
  highlighted and treated as constant references
- **Floating-point numbers**: Numbers with a decimal point are recognized in the ISS and ISPP grammar and lexer
- **Brace matching and bracket support**: Matching bracket and brace pairs are highlighted in the editor
- **Key position detection in completion**: Completion reliably distinguishes between key and value position in
  directive and parameter lines
- **Trailing semicolons**: Trailing `;` in parameter lines (e.g. `[Files]`) are accepted and processed correctly
- **Boolean highlighting**: Boolean values (`yes`/`no`) are highlighted as a dedicated token type
- **Grammar error recovery**: The parser recovers more robustly from malformed lines; dangling tokens in parameter
  sections are handled separately
- **Fold ranges**: Correct calculation of folding ranges taking CRLF line endings into account
- **Icons**: Revised and improved file and element icons
- **Commenter**: Line comments (`; …`) are toggled via the standard commenter shortcut

### Compatibility

- K2 compiler support (IntelliJ-internal Kotlin analysis)
- Updated JetBrains IDE compatibility

### Fixed

- Syntax highlighting colors not displaying correctly in non-IntelliJ IDEs (e.g. Rider): all token colors are
  now defined explicitly for both Darcula and Light themes instead of relying on IntelliJ-internal color key
  inheritance
