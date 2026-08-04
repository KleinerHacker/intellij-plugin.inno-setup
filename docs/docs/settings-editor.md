# Editor Settings

The **Editor** sub-page under the **Inno Setup** settings node controls how `.iss` scripts are presented
in the editor.

![Inno Setup Editor Settings](assets/images/settings_editor.png)

---

## Inlay Hints

| Option                                  | Description                                                                                                                                                                                                                                                                             |
|-----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Show #include content as inlay hint** | Shows the resolved content of an `#include "…"` directive as an inlay hint **below** the directive line, keeping the included file's line breaks. Click the hint to jump into the included file. No hint is shown when the target file cannot be found or read. **Enabled by default.** |

These settings are **IDE-wide** (stored in the global IDE configuration, not per project).

The **computed `#define` value** hint is not listed here: it uses the platform's declarative inlay API and is
therefore switched on and off with every other inlay hint under **Settings | Editor | Inlay Hints**. See
[`#define`](preprocessor/define.md) for what it shows.

---

## General Settings

See [Settings](settings.md) for the installation directory and version validation, and
[Build Settings](settings-build.md) for the project-scoped build options.
