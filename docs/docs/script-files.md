# Inno Setup Script Files

The plugin supports Inno Setup script files (`.iss`) as the main installer definition format. Script files describe what
the installer builds, what it installs, which languages it offers, and which runtime actions are executed by Setup and
Uninstall.

---

## Supported Sections

`.iss` files may contain:

- `[Setup]` for installer metadata, output settings, wizard behavior, privileges, signing, compression, and version
  requirements
- `[Types]`, `[Components]`, and `[Tasks]` for selectable install modes, feature groups, and optional actions
- `[Dirs]`, `[Files]`, `[Icons]`, `[Registry]`, `[INI]`, `[InstallDelete]`, and `[UninstallDelete]` for filesystem,
  shortcut, registry, INI, and cleanup operations
- `[Run]` and `[UninstallRun]` for commands executed during install or uninstall
- `[Languages]`, `[LangOptions]`, `[Messages]`, and `[CustomMessages]` for multilingual installers and localized text
- `[ISSigKeys]` for public keys used by `.issig` signature verification
- `[Code]` for Pascal Script custom logic

`[Setup]` is the central script section and is required for normal installer scripts. Other sections are optional and
can be combined as needed for the installer workflow.

---

## Script-Aware Editing

The plugin uses its bundled Inno Setup spec to provide completion, validation, documentation popups, and section-specific
parameter suggestions. Common parameters such as `Components`, `Tasks`, `Languages`, `Check`, `MinVersion`, and
`OnlyBelowVersion` resolve to the relevant script concepts where applicable.

References are supported for script-owned names such as components, tasks, types, ISPP definitions, language prefixes,
and custom messages. Constants like `{app}`, `{autopf}`, `{group}`, and `{cm:...}` are recognized inside values, with
completion and unresolved-reference highlighting where the plugin can resolve the target.

---

## Preprocessor And Code

ISPP preprocessor directives are injected into script files and receive their own highlighting, completion, rename, and
find-usages support for definitions. The `[Code]` section is treated as Pascal Script source for installer runtime
customization.

