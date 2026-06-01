# Inno Setup – IntelliJ Plugin

**Language support for Inno Setup scripts (`.iss`) right inside IntelliJ IDEA.**

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

This IntelliJ IDEA plugin brings first-class editor support for `.iss` files:

- **Syntax highlighting** — sections, directives, parameters, values, constants, and Pascal code blocks are coloured distinctly
- **Code completion** — section headers, parameter names, and known values are suggested as you type
- **Inline documentation** — hover over any directive or parameter to read its description without leaving the IDE
- **Reference resolution** — navigate between component, task, and type definitions across the script
- **Structure view** — get a bird's-eye view of all sections and their entries in the project tool window

---

## Installation

The plugin is available on the **JetBrains Marketplace**.

> :octicons-megaphone-16: Marketplace listing coming soon.

---

## Sections Reference

The [Sections](sections/setup.md) tab above gives a complete reference for every Inno Setup section and its parameters, including type information and links to the official Inno Setup documentation.
