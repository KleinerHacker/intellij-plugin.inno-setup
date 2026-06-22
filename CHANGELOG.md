<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Inno-setup Changelog

## [Unreleased]

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
- **Documentation coverage tracking**: Added `DOC_STATUS.md` to track official Inno Setup documentation coverage.
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
