# Settings

The plugin adds an **Inno Setup** node under **Build, Execution, Deployment** in the IDE settings
(**File → Settings** on Windows/Linux, **IntelliJ IDEA → Settings** on macOS).

---

## Inno Setup Installation

![Inno Setup Settings](assets/images/settings.png)

| Option                     | Description                                                                             |
|----------------------------|-----------------------------------------------------------------------------------------|
| **Installation directory** | Path to the Inno Setup installation folder. Must contain `ISCC.exe` and `Compil32.exe`. |
| **Detected version**       | Read-only field showing the Inno Setup version found in the selected directory.         |

### Version Validation

| Option                         | Description                                                                                                              |
|--------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| **Minimum Inno Setup version** | Constrains the available version options to the detected major version. Used for version-aware warnings in `.iss` files. |

These settings are **IDE-wide** (stored in the global IDE configuration, not per project).

---

## Editor Settings

See [Editor Settings](settings-editor.md) for editor-presentation options such as the `#include` content
inlay hint.

---

## Build Settings

See [Build Settings](settings-build.md) for the project-scoped build options.
