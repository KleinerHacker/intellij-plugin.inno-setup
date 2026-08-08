# [Code]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=scriptintro){
.md-button .md-button--primary }

The `[Code]` section is where the full power of Inno Setup's scripting engine comes in. Unlike every other section, it
does not use `Key=Value` or `Key: Value` syntax — it contains free-form Pascal source code compiled and executed by the
installer at runtime using *RemObjects Pascal Script*.

Through event functions such as `InitializeSetup`, `NextButtonClick`, `CurStepChanged`, and `PrepareToInstall`, you can
intercept virtually every stage of the installation wizard, perform custom checks, download files, display custom pages,
write to the registry, and much more.

!!! info "No parameters"
The `[Code]` section has no structured parameters. Its entire content is Pascal source code. See
the [Inno Setup Scripting Reference](https://jrsoftware.org/ishelp/index.php?topic=scriptintro) for the full API
surface, including all available event functions, built-in procedures, and supported Pascal language features.

!!! note "No misplaced editor assistance inside `[Code]`"
Because the content is plain Pascal, the plugin deliberately switches off the ISS-specific editor features
here: no quick documentation, no cross-section or custom-message references, no auto-closing of `"` (Pascal
strings use `'`), and no *Flip parameters* intention. The preprocessor remains active — `#…` directive lines
and inline `{#…}` emissions keep their documentation and references, because Inno Setup evaluates ISPP inside
`[Code]` as well.
