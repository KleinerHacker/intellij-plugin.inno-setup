# Inno Setup – JetBrains Plugin

**First-class editor support for Inno Setup scripts (`.iss`) in all JetBrains IDEs.**

---

## What is Inno Setup?

[Inno Setup](https://jrsoftware.org/isinfo.php) is a free, open-source Windows installer builder created by Jordan Russell and Martijn Laan. First released in 1997, it has grown into one of the most widely used installer tools in the Windows ecosystem — powering the installers of projects like **Visual Studio Code**, **Git for Windows**, and **Embarcadero Delphi**.

Inno Setup scripts (`.iss`) describe the complete installer configuration: which files to install, which registry keys to create, which shortcuts to add, and how the installation wizard should behave. They support a rich set of sections, parameters, and an integrated Pascal scripting engine for full runtime customisation.

!!! tip "Official Inno Setup Resources"
    - :octicons-home-16: [Homepage](https://jrsoftware.org/isinfo.php)
    - :octicons-book-16: [Documentation](https://jrsoftware.org/ishelp/)
    - :octicons-download-16: [Download](https://jrsoftware.org/isdl.php)

---

## What this Plugin does

This plugin brings first-class editor support for `.iss` files to any JetBrains IDE — including **IntelliJ IDEA**, **PyCharm**, **CLion**, **Rider**, **WebStorm**, **GoLand**, and others:

- **Syntax highlighting** — sections, directives, parameters, values, constants, and Pascal code blocks are coloured distinctly
- **Code completion** — section headers, parameter names, and known values are suggested as you type
- **Inline documentation** — hover over any directive or parameter to read its description without leaving the IDE
- **Reference resolution** — navigate between component, task, and type definitions across the script
- **Structure view** — get a bird's-eye view of all sections and their entries in the project tool window
- **Constant support** — `{app}`, `{autopf}`, `{group}`, and all other built-in constants are recognised and validated, including inside quoted strings

---

## IDE Compatibility

The plugin is built against `com.intellij.modules.lang`, the language-support module that is present in every full JetBrains IDE. It does **not** require IntelliJ IDEA specifically, and carries its own YAML parsing infrastructure so it has no hidden runtime dependencies on the host IDE.

| IDE | Supported |
|---|---|
| IntelliJ IDEA (Community & Ultimate) | ✔ |
| PyCharm (Community & Professional) | ✔ |
| CLion / CLion Nova | ✔ |
| Rider | ✔ |
| WebStorm | ✔ |
| GoLand | ✔ |
| RubyMine | ✔ |
| DataGrip | ✔ |
| Other IntelliJ-platform IDEs | ✔ |

---

## Installation

The plugin is **not yet published on the JetBrains Marketplace**. Install it manually from a locally built JAR/ZIP:

### 1 · Build the plugin

```bash
./gradlew buildPlugin
```

The distributable ZIP is written to `build/distributions/`.

### 2 · Install in your IDE

1. Open **Settings / Preferences → Plugins**
2. Click the ⚙ gear icon and choose **Install Plugin from Disk…**
3. Select the ZIP file from `build/distributions/`
4. Restart the IDE when prompted

!!! note "Marketplace listing"
    A JetBrains Marketplace release is planned. Once available, the plugin can be installed directly from the IDE's built-in plugin browser.

---

## Sections Reference

The [Sections](sections/setup.md) tab above gives a complete reference for every Inno Setup section and its parameters, including type information and links to the official Inno Setup documentation.
