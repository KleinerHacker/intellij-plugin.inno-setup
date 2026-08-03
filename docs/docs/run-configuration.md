# Run Configuration

The plugin provides a dedicated **Inno Setup** run configuration type that compiles an `.iss` script and
immediately launches the generated installer — all from within the IDE.

---

## Creating a Run Configuration

The run configuration is created automatically when you **right-click an `.iss` file** in the Project view
and choose **Run** or **Debug**. You can also create one manually via **Run → Edit Configurations…**,
click **+**, and select **Inno Setup**.

![Inno Setup Run Configuration](assets/images/run.png)

---

## Configuration Options

| Option           | Description                                                                                                                                    |
|------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| **Script**       | Path to the top-level `.iss` script to compile. The drop-down lists all top-level scripts found in the project; any path may also be typed in. |
| **Language**     | Selects the installer language at launch time. Only shown when the script's `[Languages]` section contains two or more entries.                |
| **Debug output** | When enabled, passes `/DBGMSG` to the installer so diagnostic messages are printed to the console during installation.                         |
| **Build configuration** | The named set of compile options this run compiles with — preprocessor symbols, output directory override and additional `ISCC` options. Only the *name* is stored in the run configuration; the options themselves live in [Build Settings](settings-build.md), so editing them takes effect in every run that refers to them. A new run configuration starts on **Debug**. |

---

## Running with a Build Configuration

Because the build configuration is the one thing a run still has to be told, every entry point offers the
choice:

- The **gutter icon** next to the `[Setup]` section opens a list of all build configurations; picking one
  runs the script with it.
- The **context menu** of an `.iss` file (Project view or editor) shows **Run Script** and **Build Script**
  as submenus, each listing the build configurations.
- A saved run configuration uses the one selected in its **Build configuration** drop-down.

If the referenced build configuration has been deleted, the run reports the error instead of silently
falling back to another one.

---

## Preprocessor Symbols in the Editor

The editor decides `#ifdef` branches with the symbols of the **selected** run configuration's build
configuration, so what is dimmed in the script matches what the next run will build. Switching the
selected configuration in the toolbar re-evaluates the conditions immediately.

When no Inno Setup run configuration is selected, a banner above the script says so — no symbols can be
supplied, and the conditions stay undecided. The banner carries a drop-down of the existing Inno Setup run
configurations so the choice can be made right there.

---

## How It Works

1. The script is compiled with ISCC (same pipeline as the [Build Integration](build.md)), with the `/D`
   symbols and additional options of the selected build configuration.
2. If the project's output mode is set to **Dry build**, the plugin redirects the output to a temporary
   directory automatically so a real installer is produced and can be launched.
3. The generated `setup.exe` is executed; its process output is streamed to the **Run** tool window.

A build is reused only when the participating files, the output location **and** the content of the build
configuration are unchanged since the last successful build.
