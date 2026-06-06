<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Inno-setup Changelog

## [Unreleased]

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
