<p align="center">
  <img src="docs/assets/images/inno-setup-logo.png" alt="Inno Setup Logo" width="256"/>
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

| Feature                    | Description                                                                                                          |
|----------------------------|----------------------------------------------------------------------------------------------------------------------|
| **Syntax highlighting**    | Sections, directives, parameters, constants (`{app}`, `{autopf}`, …), and Pascal code blocks are coloured distinctly |
| **Code completion**        | Section names, directive keys, parameter keys, and known flag values are suggested as you type                       |
| **Inline documentation**   | Hover over any directive or parameter to read its description without leaving the IDE                                |
| **Reference resolution**   | Navigate between `Name:` declarations and their usages in `Tasks:`, `Components:`, and `Types:` parameters           |
| **Structure view**         | Bird's-eye overview of all sections and their entries                                                                |
| **Constant validation**    | Built-in constants are recognised and validated, including those embedded inside quoted strings                      |
| **Brace / quote matching** | Auto-closes `{`, `[`, and `"`                                                                                        |
| **Code folding**           | Sections and long parameter entries fold independently                                                               |

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

# Generate parser and lexer, then compile
./gradlew compileKotlin

# Run all tests
./gradlew test

# Build the distributable plugin ZIP
./gradlew buildPlugin
# → build/distributions/inno-setup-<version>.zip
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

```
.
├── src/
│   ├── main/
│   │   ├── kotlin/          Plugin sources (Kotlin)
│   │   └── resources/
│   │       ├── META-INF/    plugin.xml, optional config files
│   │       ├── parsing/     Grammar (.bnf) and lexer (.flex) sources
│   │       └── spec/        Inno Setup spec data (YAML)
│   └── test/
│       ├── kotlin/          Unit and integration tests
│       └── resources/       Test scripts and expected PSI trees
├── build/parsing/gen/       Generated parser, lexer, and PSI classes (auto-generated)
├── docs/                    MkDocs documentation site
├── build.gradle.kts
└── settings.gradle.kts
```

> **Note:** The files under `build/parsing/gen/` are generated automatically before compilation via
`./gradlew generateIssParser generateIssLexer`. Never edit them by hand — they are overwritten on every build.

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

[Full documentation](https://kleinerhacker.github.io/intellij-plugin.inno-setup/) — including a complete reference for every Inno Setup section and its parameters — is available at
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

[Bug reports](https://github.com/KleinerHacker/intellij-plugin.inno-setup/issues) and [pull requests](https://github.com/KleinerHacker/intellij-plugin.inno-setup/pulls) are welcome. Please open an issue first to discuss larger changes.

---

## Licenses

See [LICENSES](https://kleinerhacker.github.io/intellij-plugin.inno-setup/licences/) for details.
