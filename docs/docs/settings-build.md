# Build Settings

The **Build** sub-page is found under **Build, Execution, Deployment → Inno Setup → Build** in the IDE settings.

![Inno Setup Build Settings](assets/images/settings_build.png)

These settings are **project-scoped** (stored per project, not globally).

---

## Build

| Option                                  | Description                                                                                                       |
|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **Compile .iss files on project build** | When enabled, all top-level `.iss` scripts in the project are compiled with `ISCC` whenever the project is built. |
| **Output**                              | Controls where the compiled installer output is placed. See values below.                                         |

**Output values**

| Value                                        | Description                                                                                     |
|----------------------------------------------|-------------------------------------------------------------------------------------------------|
| **As defined in script**                     | Leaves the script's `[Setup] OutputDir` untouched — no `/O` switch is passed to `ISCC`.         |
| **Into project build directory** *(default)* | Redirects a relative `OutputDir` below the project's build folder (`out`/`target`/`build`).     |
| **Dry build — validate only, no output**     | Passes the native `ISCC` `/O-` switch so the script is validated but no setup file is produced. |

---

## Build Configurations

A **build configuration** is the Inno Setup counterpart of a C/C++ build configuration: a named set of
compile options that a [run configuration](run-configuration.md) refers to by name. The options live in
one place, so several runs can share them and editing them takes effect everywhere at once.

| Option                     | Description                                                                                                                                                    |
|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Preprocessor symbols**   | Symbols passed to the compiler as `/D`, e.g. `DEBUG` or `VERSION=2`; separate several with a comma, semicolon or space.                                        |
| **Output directory**       | Overrides where the installer is written. A relative path is resolved below the project's build folder. Leave it empty to keep the **Output** rule from above. |
| **Additional ISCC options**| Further raw `ISCC` command-line options, separated by spaces. Wrap values containing spaces in double quotes.                                                   |

Use **Add**, **Copy**, **Rename** and **Remove** to manage the list. At least one configuration must remain.

### Defaults

A project that has none yet starts with two configurations:

| Name        | Symbols | Purpose                                    |
|-------------|---------|--------------------------------------------|
| **Release** | *(none)*| A plain build with nothing defined.        |
| **Debug**   | `DEBUG` | The default of every new run configuration.|

### Storage

Each configuration is stored as a **single file in the project's `.build` directory**, next to `.idea` —
the same layout the platform uses for shareable run configurations in `.run`. One file per configuration
keeps them reviewable and mergeable, so they can be shared with the team through version control.

### Effect on the build

The content of the selected build configuration is part of the build's up-to-date check. Switching a run
from `Debug` to `Release`, or merely editing `Debug`'s symbols, therefore invalidates the previously
produced installer and forces a rebuild. Renaming a configuration does not — the name is not part of what
was compiled.
