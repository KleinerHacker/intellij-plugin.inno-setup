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

---

## How It Works

1. The script is compiled with ISCC (same pipeline as the [Build Integration](build.md)).
2. If the project's output mode is set to **Dry build**, the plugin redirects the output to a temporary
   directory automatically so a real installer is produced and can be launched.
3. The generated `setup.exe` is executed; its process output is streamed to the **Run** tool window.
