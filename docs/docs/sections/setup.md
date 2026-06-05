# [Setup]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=setupsection){
.md-button .md-button--primary }

The `[Setup]` section is the backbone of every Inno Setup script. It uses a simple `Directive=Value` format and controls
everything from the application metadata shown in Windows' Add/Remove Programs to the compression algorithm, wizard
appearance, and privilege requirements. Only `AppName` and `AppVersion` are strictly required — all other directives
have sensible defaults.

---

**Application Identity**

## AppName

`string` · **Required**

The full display name of the application. Shown throughout the installation wizard and in the Windows Add/Remove
Programs list.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_appname)

---

## AppVersion

`string` · **Required**

The version string of the application, e.g. `1.0` or `2.3.1`. Displayed in Add/Remove Programs and used for upgrade
detection logic.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_appversion)

---

## AppPublisher

`string`

The name of the publisher, shown in Add/Remove Programs.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisher)

---

## AppPublisherURL

`string`

URL of the publisher's website, shown as a clickable link in Add/Remove Programs.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisherurl)

---

## AppSupportURL

`string`

Support URL shown in Add/Remove Programs.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_appsupporturl)

---

## AppUpdatesURL

`string`

Updates or download URL shown in Add/Remove Programs.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_appupdatesurl)

---

**Compiler Output**

## Compression

`string`

Compression algorithm used to pack the installer's payload. Valid values: `lzma2/ultra64` (default, best ratio),
`bzip2`, `deflate`, `none`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_compression)

---

## OutputDir

`string`

Directory where the compiled Setup EXE is written, relative to the script file. Defaults to `Output`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_outputdir)

---

## OutputBaseFilename

`string`

Base filename (without `.exe`) of the compiled installer, e.g. `myapp-setup`. Defaults to `setup`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_outputbasefilename)

---

## SourceDir

`string`

Base directory for relative paths used in `[Files]` and other sections. Defaults to the directory containing the script.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_sourcedir)

---

## SetupIconFile

`string`

Path to an `.ico` file used as the icon for the compiled Setup EXE.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_setupiconfile)

---

## EncryptionKey

`string`

Password used to encrypt the installer's files. The installer will prompt the user for this password at runtime.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_encryptionkey)

---

**Install Location**

## DefaultDirName

`string`

Default installation directory shown on the Select Destination page, e.g. `{autopf}\MyApp`. Supports all Inno Setup
directory constants.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultdirname)

---

## DefaultGroupName

`string`

Default Start Menu folder name shown on the Select Start Menu Folder page.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultgroupname)

---

**Privileges**

## PrivilegesRequired

`string`

Required privilege level for the installation. Valid values: `admin` (default), `lowest`, `none`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequired)

---

## PrivilegesRequiredOverridesAllowed

`string`

Allows the user to override the required privilege level. Values: `commandline`, `dialog`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequiredoverridesallowed)

---

**Windows Version Constraints**

## MinVersion

`string`

Minimum Windows version required to run the installer, e.g. `6.1` for Windows 7. Use `0` to effectively disable
installation.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_minversion)

---

## ArchitecturesAllowed

`string` · **Multiple values**

Space-separated list of CPU architectures the installer may run on: `x86`, `x64`, `arm64`, `ia64`. Defaults to all.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesallowed)

---

## ArchitecturesInstallIn64BitMode

`string` · **Multiple values**

Space-separated list of architectures for which the installer runs in 64-bit mode, e.g. `x64compatible arm64`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesinstallin64bitmode)

---

**Runtime Behaviour**

## AppMutex

`string`

A mutex name the installer checks before starting. If the mutex exists, the user is warned that the application is
already running.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_appmutex)

---

## CloseApplications

`boolean`

When `yes`, the installer automatically attempts to close applications that have files locked for update.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_closeapplications)

---

## LicenseFile

`string`

Path to a `.txt` or `.rtf` license file displayed on the License Agreement wizard page.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_licensefile)

---

## InfoBeforeFile

`string`

Path to a `.txt` or `.rtf` file shown on an information page before installation begins.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_infobeforefile)

---

## InfoAfterFile

`string`

Path to a `.txt` or `.rtf` file shown on an information page after installation completes.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_infoafterfile)

---

**Uninstaller**

## AllowNoIcons

`boolean`

When `yes`, a *Don't create a Start Menu folder* checkbox appears on the Select Start Menu Folder page.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_allownoicons)

---

## UninstallDisplayName

`string`

The name shown for the application in Add/Remove Programs. Defaults to `AppName`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayname)

---

## UninstallDisplayIcon

`string`

Path to an EXE, DLL, or ICO used as the icon in Add/Remove Programs, e.g. `{app}\MyApp.exe`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayicon)

---

## CreateUninstallRegKey

`boolean`

Controls whether the uninstall registry key is created under `HKLM\Software\Microsoft\Windows\CurrentVersion\Uninstall`.
Defaults to `yes`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_createuninstallregkey)

---

## Uninstallable

`string`

A Pascal expression or `yes`/`no` value controlling whether an uninstaller entry is created. Defaults to `yes`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstallable)

---

**Wizard Pages**

## DisableDirPage

`string`

Controls visibility of the Select Destination Directory page. Valid values: `yes`, `no`, `auto` (default).

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_disabledirpage)

---

## DisableProgramGroupPage

`string`

Controls visibility of the Select Start Menu Folder page. Valid values: `yes`, `no`, `auto` (default).

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_disableprogramgrouppage)

---

## DisableWelcomePage

`boolean`

When `yes`, the Welcome page is skipped entirely.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_disablewelcomepage)

---

## DisableReadyPage

`boolean`

When `yes`, the Ready to Install page is skipped entirely.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_disablereadypage)

---

## UsePreviousAppDir

`boolean`

When `yes`, the previously used installation directory is pre-filled on reinstall. Defaults to `yes`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousappdir)

---

## UsePreviousGroup

`boolean`

When `yes`, the previously used Start Menu folder name is pre-filled on reinstall. Defaults to `yes`.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousgroup)

---

## UserInfoPage

`boolean`

When `yes`, a page asking for the user's name, company name, and an optional serial number is shown.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_userinfopage)

---

**Wizard Appearance**

## WizardStyle

`string`

Visual style of the wizard. Valid values: `classic`, `modern` (default).

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardstyle)

---

## WizardResizable

`boolean`

When `yes`, the wizard window can be resized by the user.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardresizable)

---

## WizardImageFile

`string`

Path to a BMP file displayed on the left side of the wizard in classic style.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardimagefile)

---

## WizardSmallImageFile

`string`

Path to a BMP file displayed in the top-right corner of the wizard.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardsmallimagefile)

---

## SetupLogging

`boolean`

When `yes`, a log file is automatically created in the user's temp directory during installation.

[:octicons-link-external-16: Reference](https://jrsoftware.org/ishelp/index.php?topic=setup_setuplogging)
