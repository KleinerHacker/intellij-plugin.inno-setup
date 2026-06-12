# Build Settings

The **Build** sub-page is found under **Build, Execution, Deployment → Inno Setup → Build** in the IDE settings.

![Inno Setup Build Settings](assets/images/settings_build.png)

These settings are **project-scoped** (stored per project, not globally).

---

## Build

| Option                                  | Description                                                                                                                                                               |
|-----------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Compile .iss files on project build** | When enabled, all top-level `.iss` scripts in the project are compiled with `ISCC` whenever the project is built.                                                         |
| **Output**                              | Controls where the compiled installer output is placed. See values below.                                                                                                  |

**Output values**

| Value | Description |
|---|---|
| **As defined in script** | Leaves the script's `[Setup] OutputDir` untouched — no `/O` switch is passed to `ISCC`. |
| **Into project build directory** *(default)* | Redirects a relative `OutputDir` below the project's build folder (`out`/`target`/`build`). |
| **Dry build — validate only, no output** | Passes the native `ISCC` `/O-` switch so the script is validated but no setup file is produced. |
