<p align="center">
  <img src="docs/docs/assets/images/inno-setup-logo.png" alt="Inno Setup Logo" width="256"/>
</p>

# Inno Setup – JetBrains Plugin

A JetBrains IDE plugin that brings first-class language support for [Inno Setup](https://jrsoftware.org/isinfo.php)
scripts (`.iss`) to the entire IntelliJ platform family.

---

## About

[Inno Setup](https://jrsoftware.org/isinfo.php) is a widely-used, free Windows installer builder by Jordan Russell and
Martijn Laan (first released 1997). Its scripts (`.iss`) describe the full installer — files, registry keys, shortcuts,
and optional Pascal scripting — but until now had no dedicated editor support inside JetBrains IDEs.

This plugin closes that gap. The goal is a complete editing experience for `.iss` files: correct highlighting,
context-aware completion, inline documentation, and validated references, regardless of which JetBrains IDE you are
using.

### Features

| Feature                    | Description                                                                                                                                                                           |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Syntax highlighting**    | Sections, directives, parameters, constants (`{app}`, `{autopf}`, …), and Pascal code blocks are coloured distinctly                                                                  |
| **Code completion**        | Section names, directive keys, parameter keys, and known flag values are suggested as you type                                                                                        |
| **Inline documentation**   | Hover over any directive or parameter to read its description without leaving the IDE                                                                                                 |
| **Reference resolution**   | Navigate between `Name:` declarations and their usages in `Tasks:`, `Components:`, and `Types:` parameters                                                                            |
| **Structure view**         | Bird's-eye overview of all sections and their entries                                                                                                                                 |
| **Constant validation**    | Built-in constants are recognised and validated, including those embedded inside quoted strings                                                                                       |
| **Brace / quote matching** | Auto-closes `{`, `[`, and `"`                                                                                                                                                         |
| **Code folding**           | Sections, long parameter entries, and `#if … #endif` blocks fold independently                                                                                                        |
| **Code formatting**        | Reformat Code normalises spacing around `=` / `:` / `;` and `[ ]`, keeps one blank line between sections, and spaces preprocessor arithmetic operators; configurable under Code Style  |
| **Inlay hints**            | Language flag icons are shown inline next to `Languages:` parameter values                                                                                                            |
| **Build integration**      | Compile `.iss` scripts directly via a context-menu action; optionally run ISCC automatically on project build                                                                         |
| **Language file support**  | `.isl` language files are recognised, highlighted, and validated alongside `.iss` scripts                                                                                             |
| **ISPP support**           | Preprocessor directives (`#define`/`#undef` with scope keywords, `#include`, `#if`/`#elif`/`#else`/`#endif`, …) are parsed, highlighted, completed, validated, and reference-resolved |

### IDE Compatibility

The plugin targets `com.intellij.modules.lang` — available in every full IntelliJ-platform IDE — and bundles its own
runtime dependencies, so it has no hidden requirements on the host IDE.

Works in: **IntelliJ IDEA**, **PyCharm**, **CLion / CLion Nova**, **Rider**, **WebStorm**, **GoLand**, **RubyMine**, *
*DataGrip**, and all other IntelliJ-platform IDEs.

---

## Getting Started (Development)

### Prerequisites

| Tool          | Version                                              |
|---------------|------------------------------------------------------|
| JDK           | 21 or later                                          |
| IntelliJ IDEA | 2024.1 or later (for IDE-assisted development)       |
| Gradle        | Provided via Gradle Wrapper — no installation needed |

### Build

```bash
# Clone the repository
git clone https://github.com/KleinerHacker/inno-setup.git
cd inno-setup

# Generate parsers/lexers and compile every module
./gradlew assemble

# Run all tests (they live in the :plugin module)
./gradlew :plugin:test

# Build the distributable plugin ZIP
./gradlew :plugin:buildPlugin
# → plugin/build/distributions/inno-setup-<version>.zip
```

### Run in a sandboxed IDE

```bash
./gradlew runIde
```

This launches a fresh IntelliJ IDEA instance with the plugin loaded, isolated from your regular IDE installation. Open
or create any `.iss` file to try the plugin live.

### Run / Debug from IntelliJ IDEA

Preconfigured run configurations are included in `.run/`:

| Configuration         | What it does                                             |
|-----------------------|----------------------------------------------------------|
| **Run Plugin**        | Launches `:runIde` — opens a sandbox IDE with the plugin |
| **Run Tests**         | Runs `:test`                                             |
| **Run Verifications** | Runs `:verifyPlugin` to check compatibility              |

### Project Structure

A **Gradle multi-module** build with the dependency chain `:plugin → :language:script → :language:preprocessor`.
The root project is a pure aggregator (no code, no `plugin.xml`).

```
.
├── language/
│   ├── preprocessor/        ISPP preprocessor language (lexer/parser/PSI, highlighter, annotator,
│   │                        brace matcher, references, expression engine, ISPP spec, PluginBundle)
│   │   └── src/main/{kotlin, resources/{META-INF, parsing, spec, messages}}
│   └── script/              Inno Setup language: section/INI grammar (.iss/.isl/.ist), file types,
│       │                    highlighter, folding, annotator, references, include infra, ISPP injector,
│       │                    spec/settings services
│       └── src/main/{kotlin, resources/{META-INF, parsing, spec, icons}}
├── plugin/                  Publishable plugin: IDE features, build/run, settings UI, main plugin.xml,
│   │                        color schemes, icons — and ALL tests
│   └── src/{main, test}/
├── buildSrc/                Shared Gradle convention (inno-setup.platform-module)
├── <module>/build/generated/  Generated parser/lexer/PSI per module (auto-generated)
├── docs/                    MkDocs documentation site
├── build.gradle.kts         Root aggregator (Dokka over all modules, kover merge, MkDocs, generateSources)
└── settings.gradle.kts
```

> **Note:** Generated sources live per-module under `<module>/build/generated/`. Regenerate them via
`./gradlew generateSources` (root umbrella) or the per-module `generateIs*Parser`/`generateIs*Lexer` tasks.
> Never edit them by hand — they are overwritten on every build.

---

## Manual Installation

The plugin is **not yet available on the JetBrains Marketplace**. Install it manually from the built ZIP:

### Step 1 — Build the plugin ZIP

```bash
./gradlew buildPlugin
```

The output is written to `build/distributions/inno-setup-<version>.zip`.

### Step 2 — Install in your IDE

1. Open your JetBrains IDE and go to **Settings / Preferences → Plugins**
2. Click the **⚙ gear icon** in the top-right corner of the Plugins panel
3. Choose **Install Plugin from Disk…**
4. Navigate to `build/distributions/` and select the `.zip` file
5. Click **OK**, then **Restart IDE** when prompted

After the restart, any file with the `.iss` extension will be handled by the plugin automatically.

---

## Documentation

[Full documentation](https://kleinerhacker.github.io/intellij-plugin.inno-setup/) — including a complete reference for
every Inno Setup section and its parameters — is available at
the project's MkDocs site, hosted on GitHub Pages.

To run the documentation site locally:

```bash
# Install dependencies (once)
cd docs
pip install mkdocs mkdocs-material

# Serve locally
mkdocs serve
```

Then open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser.

> [API Documentation](https://kleinerhacker.github.io/intellij-plugin.inno-setup/dokka/html/) is availabe, too.

---

## Contributing

[Bug reports](https://github.com/KleinerHacker/intellij-plugin.inno-setup/issues)
and [pull requests](https://github.com/KleinerHacker/intellij-plugin.inno-setup/pulls) are welcome. Please open an issue
first to discuss larger changes.

---

## Licenses

See [LICENSES](https://kleinerhacker.github.io/intellij-plugin.inno-setup/licences/) for details.
