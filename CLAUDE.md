# Inno Setup – Knowledge Reference

Source: https://jrsoftware.org/ishelp/

## Overview

Inno Setup is an open-source Windows installer builder by Jordan Russell & Martijn Laan (since 1997, current version 7.x).
Supports Windows Server 2008 R2 through Windows 11 including Arm64.

**Key features:**
- Single EXE output, ~2 MB overhead
- Compression: deflate, bzip2, 7-Zip LZMA
- 64-bit, Arm64, multi-language support
- Registry, shortcuts, .INI file creation
- Password protection, digital signatures, silent install
- Integrated Pascal scripting ([Code] section)
- Inno Setup Preprocessor (ISPP) for compile-time automation

Notable users: VS Code, Git for Windows, Embarcadero Delphi.

---

## Script Format

Scripts consist of named sections in `[BracketName]` notation. Two types:
- **Directive sections** (`[Setup]`): `Directive=Value` pairs
- **Parameter sections** (`[Files]`, `[Registry]`, …): semicolon-separated `Key: Value` entries

```iss
[Setup]
AppName=My Program
AppVersion=1.0

[Files]
Source: "MyProg.exe"; DestDir: "{app}"
```

- Lines starting with `;` are comments
- `#include "file.txt"` inserts external files; `compiler:filename` searches compiler dir
- Multiple sections of the same name are allowed
- Unicode scripts must be UTF-8 encoded
- Preprocessing: `#preproc builtin` (only `#include`) or `#preproc ispp` (full ISPP, default when available)
- Leading/trailing whitespace in directive values is stripped unless quoted

---

## Constants

### Directory Constants
| Constant | Meaning |
|---|---|
| `{app}` | User-selected install directory |
| `{win}` | Windows directory |
| `{sys}` | System32 (32-bit view by default on 64-bit) |
| `{sysnative}` | Native 64-bit System32 on 64-bit OS |
| `{syswow64}` | SysWOW64 on 64-bit OS |
| `{src}` | Directory containing Setup files |
| `{sd}` | System drive (e.g. C:) |
| `{tmp}` | Temp dir created by Setup/Uninstall |
| `{commonpf}` / `{commonpf32}` / `{commonpf64}` | Program Files |
| `{commoncf}` / `{commoncf32}` / `{commoncf64}` | Common Files |
| `{commonfonts}` | Fonts directory |
| `{dotnet11}` / `{dotnet20}` / `{dotnet40}` | .NET Framework dirs |

### Shell Folder Constants
| Constant | Meaning |
|---|---|
| `{group}` | Start Menu folder path |
| `{localappdata}` | User's local AppData |
| `{userappdata}` / `{commonappdata}` | AppData folders |
| `{userdesktop}` / `{commondesktop}` | Desktop |
| `{userdocs}` / `{commondocs}` | My Documents |
| `{userprograms}` / `{commonprograms}` | Programs on Start Menu |
| `{userstartup}` / `{commonstartup}` | Startup folders |

### Auto Constants (resolve to "common" or "user" depending on install mode)
`{autoappdata}`, `{autocf}`, `{autodesktop}`, `{autodocs}`, `{autopf}`, `{autoprograms}`, `{autostartup}`

### Special Constants
| Constant | Meaning |
|---|---|
| `{\\}` | Literal backslash |
| `{%NAME\|Default}` | Environment variable |
| `{cmd}` | Path to cmd.exe |
| `{computername}` | Computer name |
| `{username}` | Current user name |
| `{language}` | Selected language internal name |
| `{groupname}` | Start Menu folder name only |
| `{srcexe}` | Setup program pathname |
| `{uninstallexe}` | Uninstall program pathname |
| `{log}` | Log file name |
| `{drive:Path}` | Extract drive letter |
| `{ini:File,Section,Key\|Default}` | INI file value |
| `{reg:HK_xx\\Subkey,Value\|Default}` | Registry value |
| `{param:Name\|Default}` | Command-line parameter |
| `{cm:MessageName}` | Custom message value |

---

## [Setup] Section

Required directives: **AppName**, **AppVersion**.
Whitespace in values is stripped unless quoted.

### Key Compiler Directives
| Directive | Purpose |
|---|---|
| `Compression` | Algorithm: `lzma2/ultra64`, `bzip2`, `deflate`, `none` |
| `OutputDir` | Output folder for compiled EXE |
| `OutputBaseFilename` | Output EXE filename (without .exe) |
| `SourceDir` | Source directory for relative paths |
| `SetupIconFile` | Icon for the setup EXE |
| `EncryptionKey` | Encryption password |

### Key Functional Directives
| Directive | Purpose |
|---|---|
| `AppName` | **(required)** Application name |
| `AppVersion` | **(required)** Application version string |
| `AppPublisher` | Publisher name |
| `AppPublisherURL` | Publisher URL |
| `AppSupportURL` | Support URL |
| `AppUpdatesURL` | Updates URL |
| `DefaultDirName` | Default install path, e.g. `{autopf}\\MyApp` |
| `DefaultGroupName` | Default Start Menu group |
| `PrivilegesRequired` | `admin`, `lowest`, or `none` |
| `PrivilegesRequiredOverridesAllowed` | Allow user to override: `commandline`, `dialog` |
| `MinVersion` | Minimum Windows version |
| `ArchitecturesAllowed` | Architectures: `x86 x64 arm64 ia64` |
| `ArchitecturesInstallIn64BitMode` | When to use 64-bit mode |
| `AppMutex` | Mutex name to detect running instance |
| `CloseApplications` | Auto-close apps using updated files |
| `LicenseFile` | License file to display |
| `InfoBeforeFile` | Info text before install |
| `InfoAfterFile` | Info text after install |
| `AllowNoIcons` | Show "Don't create Start Menu folder" checkbox |
| `UninstallDisplayName` | Name in Add/Remove Programs |
| `UninstallDisplayIcon` | Icon in Add/Remove Programs |
| `CreateUninstallRegKey` | Whether to create uninstall registry key |
| `Uninstallable` | Expression controlling uninstall entry |
| `DisableDirPage` | `yes`, `no`, `auto` |
| `DisableProgramGroupPage` | `yes`, `no`, `auto` |
| `DisableWelcomePage` | `yes`, `no` |
| `DisableReadyPage` | `yes`, `no` |
| `UsePreviousAppDir` | Reuse previous install dir on reinstall |
| `UsePreviousGroup` | Reuse previous Start Menu group |
| `UserInfoPage` | Show username/company/serial page |

### Cosmetic Directives
| Directive | Purpose |
|---|---|
| `WizardStyle` | `classic` or `modern` |
| `WizardResizable` | Allow resizing |
| `WizardImageFile` | Left-side bitmap in classic style |
| `WizardSmallImageFile` | Top-right bitmap |
| `SetupLogging` | Enable setup log |

---

## [Types] Section

Optional. Defines installation profiles shown in wizard.

```iss
[Types]
Name: "full";    Description: "Full installation"
Name: "compact"; Description: "Compact installation"
Name: "custom";  Description: "Custom installation"; Flags: iscustom
```

**Parameters:** `Name` (req), `Description` (req), `Flags`
**Flags:** `iscustom` — marks as user-customizable type (only one allowed)

---

## [Components] Section

Optional. Defines selectable install features. Uses backslash hierarchy (`parent\\child`).

```iss
[Components]
Name: "main";      Description: "Main Files";     Types: full compact
Name: "help";      Description: "Help Files";     Types: full
Name: "extra";     Description: "Extra Features"; Types: full custom
```

**Parameters:** `Name` (req), `Description` (req), `Types`, `ExtraDiskSpaceRequired`, `Flags`

**Flags:** `fixed`, `checkablealone`, `exclusive`, `restart`, `dontinheritcheck`, `disablenouninstallwarning`

---

## [Tasks] Section

Optional. Defines checkboxes/radio buttons in wizard. Must be linked to entries in other sections.

```iss
[Tasks]
Name: "desktopicon";        Description: "Create desktop icon"; GroupDescription: "Additional:"
Name: "desktopicon\\user";  Description: "For current user only"; Flags: exclusive
Name: "desktopicon\\common"; Description: "For all users";       Flags: exclusive
```

**Parameters:** `Name` (req), `Description` (req), `GroupDescription`, `Components`, `Flags`

**Flags:** `checkablealone`, `checkedonce`, `dontinheritcheck`, `exclusive`, `restart`, `unchecked`

---

## [Dirs] Section

Optional. Creates additional directories (mainly useful for empty dirs; `[Files]` auto-creates needed dirs).

```iss
[Dirs]
Name: "{app}\\data"
Name: "{app}\\logs"; Flags: uninsneveruninstall
```

**Parameters:** `Name` (req), `Attribs`, `Permissions`, `Flags` + Common Parameters

**Attribs:** `readonly`, `hidden`, `system`, `notcontentindexed`
**Flags:** `deleteafterinstall`, `setntfscompression`, `uninsalwaysuninstall`, `uninsneveruninstall`, `unsetntfscompression`

> Warning: Never set ACLs on top-level dirs like `{sys}` or `{commonpf}`.

---

## [Files] Section

```iss
[Files]
Source: "MyProg.exe"; DestDir: "{app}"
Source: "Readme.txt"; DestDir: "{app}"; Flags: isreadme
Source: "Plugins\\*"; DestDir: "{app}\\Plugins"; Flags: recursesubdirs createallsubdirs
```

### Parameters
| Parameter | Description |
|---|---|
| `Source` | **(req)** Source path or wildcard |
| `DestDir` | **(req)** Destination directory |
| `DestName` | Rename file on install |
| `Excludes` | Comma-separated exclusion patterns |
| `ExternalSize` | Size in bytes for external files |
| `Attribs` | `readonly`, `hidden`, `system`, `notcontentindexed` |
| `Permissions` | ACL: `users-modify`, `everyone-readexec`, etc. |
| `FontInstall` | Font registry name (installs as font) |
| `StrongAssemblyName` | .NET strong assembly name |
| `Hash` | SHA-256 hash for verification |
| `ISSigAllowedKeys` | Signature keys from [ISSigKeys] |
| `ExtractArchivePassword` | Archive password (stored unencrypted!) |
| `DownloadISSigSource` | URL for .issig file |
| `DownloadUserName` / `DownloadPassword` | HTTP Basic Auth for downloads |

### Key Flags
`32bit`, `64bit`, `comparetimestamp`, `confirmoverwrite`, `deleteafterinstall`, `dontcopy`, `download`, `external`, `extractarchive`, `ignoreversion`, `isreadme`, `nocompression`, `onlyifdoesntexist`, `recursesubdirs`, `createallsubdirs`, `regserver`, `regtypelib`, `restartreplace`, `sharedfile`

---

## [Icons] Section

Creates shortcuts (Start Menu, Desktop, etc.).

```iss
[Icons]
Name: "{group}\\My Program";          Filename: "{app}\\MyProg.exe"
Name: "{commondesktop}\\My Program";  Filename: "{app}\\MyProg.exe"; Tasks: desktopicon
```

### Parameters
| Parameter | Description |
|---|---|
| `Name` | **(req)** Shortcut path and name |
| `Filename` | **(req)** Target executable or URL |
| `Parameters` | Command-line arguments |
| `WorkingDir` | Initial working directory |
| `HotKey` | Launch hotkey, e.g. `ctrl+alt+k` |
| `Comment` | Tooltip text |
| `IconFilename` | Custom icon file (.ico/.exe/.dll) |
| `IconIndex` | Zero-based icon index |
| `AppUserModelID` | Windows 7+ app ID |
| `AppUserModelToastActivatorCLSID` | Windows 10+ toast CLSID |
| `Flags` | `runminimized`, `runmaximized`, `closeonexit`, `createonlyiffileexists`, `preventpinning` |

---

## [Registry] Section

```iss
[Registry]
Root: HKLM; Subkey: "Software\\My Company\\My App"; ValueType: string; ValueName: "Version"; ValueData: "{#AppVersion}"
Root: HKLM; Subkey: "Software\\My Company\\My App"; Flags: uninsdeletekey
```

### Parameters
| Parameter | Description |
|---|---|
| `Root` | **(req)** `HKCU`, `HKLM`, `HKCR`, `HKU`, `HKCC`, `HKA`; suffix `32`/`64` for view |
| `Subkey` | **(req)** Registry key path |
| `ValueType` | `none` (key only), `string`, `expandsz`, `multisz`, `dword`, `qword`, `binary` |
| `ValueName` | Value name (empty = default value) |
| `ValueData` | Data; `{olddata}` appends to existing; `{break}` for multisz lines |
| `Permissions` | ACL: `full`, `modify`, `read` |
| `Flags` | See below |

### Flags
`createvalueifdoesntexist`, `deletekey`, `deletevalue`, `dontcreatekey`, `noerror`, `preservestringtype`, `uninsclearvalue`, `uninsdeletekey`, `uninsdeletekeyifempty`, `uninsdeletevalue`

---

## [Run] / [UninstallRun] Sections

`[Run]` executes programs after successful install (before final dialog).
`[UninstallRun]` executes during uninstallation startup. Entries run sequentially.

```iss
[Run]
Filename: "{app}\\Init.exe"; Parameters: "/x"
Filename: "{app}\\Readme.txt"; Description: "View README"; Flags: postinstall shellexec skipifsilent
```

### Parameters
| Parameter | Description |
|---|---|
| `Filename` | **(req)** Executable, file, or folder |
| `Description` | Text for checkbox on completion page (`postinstall` only) |
| `Parameters` | Command-line arguments |
| `WorkingDir` | Working directory (default: Filename's dir) |
| `StatusMsg` | Status message during execution |
| `RunOnceId` | [UninstallRun] only: prevents duplicate execution |
| `Verb` | Shell verb: `open`, `print` (requires `shellexec`) |
| `OnLog` | Callback procedure per output line (requires `logoutput`) |

### Key Flags
`postinstall`, `shellexec`, `nowait`, `runhidden`, `skipifsilent`, `skipifnotsilent`, `unchecked`, `waituntilterminated`, `waituntilidle`, `logoutput`, `runasoriginaluser`

---

## Common Parameters (available in most sections)

| Parameter | Description |
|---|---|
| `Components` | Space-separated component names; supports `not`/`and`/`or` |
| `Tasks` | Space-separated task names; supports `not`/`and`/`or` |
| `Languages` | Space-separated language names |
| `Check` | Pascal function name returning Boolean; entry processed only if true |
| `BeforeInstall` | Pascal procedure called before entry is processed |
| `AfterInstall` | Pascal procedure called after entry is processed |
| `MinVersion` | Minimum Windows version (0 = never) |
| `OnlyBelowVersion` | Maximum Windows version (0 = no limit) |

---

## [Languages] Section

```iss
[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german";  MessagesFile: "compiler:Languages\\German.isl"
```

---

## Pascal Scripting ([Code] Section)

Runtime customization using Delphi-like Pascal (RemObjects Pascal Script). No compile-time functionality.
Has integrated debugger.

### Setup Event Functions
| Function | Signature | Purpose |
|---|---|---|
| `InitializeSetup` | `(): Boolean` | Return false to abort setup |
| `InitializeWizard` | `()` | Modify wizard pages at startup |
| `DeinitializeSetup` | `()` | Called before setup terminates |
| `CurStepChanged` | `(CurStep: TSetupStep)` | `ssInstall`, `ssPostInstall`, `ssDone` |
| `CurInstallProgressChanged` | `(CurProgress, MaxProgress: Integer)` | Progress monitoring |
| `NextButtonClick` | `(CurPageID: Integer): Boolean` | Control page advance |
| `BackButtonClick` | `(CurPageID: Integer): Boolean` | Control page back |
| `CancelButtonClick` | `(CurPageID: Integer; var Cancel, Confirm: Boolean)` | Customize cancel |
| `ShouldSkipPage` | `(PageID: Integer): Boolean` | Return true to skip page |
| `CurPageChanged` | `(CurPageID: Integer)` | After page becomes visible |
| `CheckPassword` | `(Password: String): Boolean` | Validate password |
| `NeedRestart` | `(): Boolean` | Prompt for restart |
| `UpdateReadyMemo` | `(...: String): String` | Customize "Ready to Install" text |
| `RegisterPreviousData` | `(PreviousDataKey: Integer)` | Store wizard settings |
| `CheckSerial` | `(Serial: String): Boolean` | Validate serial number |
| `GetCustomSetupExitCode` | `(): Integer` | Custom exit code on success |
| `PrepareToInstall` | `(var NeedsRestart: Boolean): String` | Pre-install checks; return error msg to halt |
| `RegisterExtraCloseApplicationsResources` | `()` | Register files for in-use checking |

### Uninstall Event Functions
| Function | Signature | Purpose |
|---|---|---|
| `InitializeUninstall` | `(): Boolean` | Return false to abort |
| `InitializeUninstallProgressForm` | `()` | Modify uninstall progress form |
| `DeinitializeUninstall` | `()` | Before uninstall terminates |
| `CurUninstallStepChanged` | `(CurUninstallStep: TUninstallStep)` | `usAppMutexCheck`, `usUninstall`, `usPostUninstall`, `usDone` |
| `UninstallNeedRestart` | `(): Boolean` | Prompt for restart after uninstall |

### Page IDs (predefined)
`wpWelcome`, `wpLicense`, `wpPassword`, `wpInfoBefore`, `wpUserInfo`, `wpSelectDir`, `wpSelectComponents`, `wpSelectProgramGroup`, `wpSelectTasks`, `wpReady`, `wpPreparing`, `wpInstalling`, `wpInfoAfter`, `wpFinished`

### Wizard Page Creation Functions
```pascal
CreateInputQueryPage(AfterID, Caption, Desc, SubCaption)     // Text input
CreateInputOptionPage(AfterID, Caption, Desc, SubCaption, Exclusive, ListBox) // Checkboxes/radio
CreateInputDirPage(AfterID, Caption, Desc, SubCaption, AppendDir, NewFolderName)
CreateInputFilePage(AfterID, Caption, Desc, SubCaption)
CreateOutputMsgPage(AfterID, Caption, Desc, Msg)
CreateOutputMsgMemoPage(AfterID, Caption, Desc, SubCaption, Msg)
CreateOutputProgressPage(Caption, Desc)
CreateDownloadPage(Caption, Desc, OnDownloadProgress)
CreateExtractionPage(Caption, Desc, OnExtractionProgress)
CreateCustomPage(AfterID, Caption, Desc)
```

### Key Script Functions (selection)

**File System:**
`DirExists`, `FileExists`, `FileSize64`, `GetSpaceOnDisk64`, `FindFirst/FindNext/FindClose`, `CopyFile`, `DeleteFile`, `RenameFile`, `ForceDirectories`, `DelTree`, `LoadStringFromFile`, `SaveStringToFile`, `LoadStringsFromFile`, `SaveStringsToFile/UTF8`

**Execution:**
`Exec(Filename, Params, WorkingDir, ShowCmd, Wait, ResultCode)`,
`ShellExec(Verb, Filename, Params, WorkingDir, ShowCmd, Wait, ErrorCode)`,
`ExecAndCaptureOutput(...)`, `ExecAndLogOutput(...)`

**Registry:**
`RegKeyExists`, `RegValueExists`, `RegQueryStringValue`, `RegQueryDWordValue`, `RegWriteStringValue`, `RegWriteDWordValue`, `RegDeleteKeyIncludingSubkeys`, `RegDeleteValue`

**String:**
`Format`, `Trim`, `Copy`, `Pos`, `RPos`, `LowerCase`, `UpperCase`, `StringChange`, `StringSplit`, `StringJoin`, `AddBackslash`, `ExtractFilePath`, `ExtractFileName`, `ExtractFileExt`, `ChangeFileExt`, `ExpandFileName`

**System Info:**
`IsAdmin`, `IsAdminInstallMode`, `Is64BitInstallMode`, `IsWin64`, `IsX64OS`, `IsArm64`, `GetWindowsVersionEx`, `IsDotNetInstalled`, `GetEnv`, `GetUserNameString`

**Dialogs:**
`MsgBox(Text, Typ, Buttons)`, `TaskDialogMsgBox(...)`, `GetOpenFileName`, `BrowseForFolder`

**Setup Info:**
`ExpandConstant(S)`, `WizardDirValue`, `WizardIsComponentSelected`, `WizardIsTaskSelected`, `WizardSilent`, `GetPreviousData`, `SetPreviousData`, `ActiveLanguage`, `Log(S)`

**Downloads:**
`DownloadTemporaryFile(Url, BaseName, SHA256, OnProgress)`,
`ExtractTemporaryFile(FileName)`, `ExtractTemporaryFiles(Pattern)`

**COM:**
`CreateOleObject(ClassName)`, `GetActiveOleObject(ClassName)`

**Hashing:**
`GetSHA256OfFile`, `GetSHA256OfString`, `GetMD5OfFile`, `GetSHA1OfFile`

---

## Inno Setup Preprocessor (ISPP)

Compile-time automation. Active by default when ISPP is installed.

### Directives
| Directive | Purpose |
|---|---|
| `#define Name Value` | Define variable |
| `#define Name(params) expr` | User-defined function |
| `#dim` / `#redim` | Declare/resize array |
| `#undef Name` | Remove definition |
| `#include "file"` | Include external file |
| `#emit expr` | Output expression value |
| `#expr expr` | Evaluate expression (no output) |
| `#if` / `#elif` / `#else` / `#endif` | Conditional compilation |
| `#ifdef` / `#ifndef` | Check if defined |
| `#ifexist` / `#ifnexist` | Check file existence |
| `#for` | Loop |
| `#sub Name` / `#endsub` | Define subroutine |
| `#pragma` | Compiler options |
| `#error msg` | Generate compile error |

### Inline syntax
```iss
#define AppVersion "1.0.0"
AppName=My App {#AppVersion}
; or explicitly:
AppName=My App {#emit AppVersion}
```

### Predefined Variables
`__FILE__`, `__LINE__`, `__DATE__`, `__TIME__`, `__COMPILER__`, `__VERSION__`

### Built-in Functions (selection)
`GetFileVersion(Filename)` — read version from EXE/DLL
`GetStringFileInfo(Filename, InfoKey)` — read string from version info
`FileExists(Filename)`, `DirExists(Path)` — file system checks
`Int(expr)`, `Str(expr)` — type conversion
`Lowercase(str)`, `Uppercase(str)`, `Copy(str, from, len)`, `Pos(substr, str)`

---

## [InstallDelete] / [UninstallDelete] Sections

Delete files/dirs during install or uninstall.

```iss
[UninstallDelete]
Type: filesandordirs; Name: "{app}\\cache"
Type: files;          Name: "{app}\\*.log"
```

**Type values:** `files`, `filesandordirs`, `dirifempty`

---

## Setup Command Line Parameters

| Parameter | Description |
|---|---|
| `/SILENT` | No wizard, shows progress |
| `/VERYSILENT` | No wizard, no progress window |
| `/SUPPRESSMSGBOXES` | Suppress message boxes (with silent modes) |
| `/NORESTART` | Prevent restart |
| `/RESTARTEXITCODE=n` | Custom exit code when restart needed |
| `/DIR="path"` | Override install directory |
| `/GROUP="name"` | Override Start Menu group |
| `/NOICONS` | Check "Don't create Start Menu folder" |
| `/TYPE=type` | Override setup type |
| `/COMPONENTS="a,b"` | Select components |
| `/TASKS="a,b"` | Set task selections |
| `/MERGETASKS="a,b"` | Merge task selections with defaults |
| `/PASSWORD=pw` | Provide password |
| `/LOG` | Create log in %TEMP% |
| `/LOG="path"` | Create log at path |
| `/LANG=name` | Set language |
| `/LOADINF="file"` | Load settings from INF file |
| `/SAVEINF="file"` | Save settings to INF file |
| `/ALLUSERS` / `/CURRENTUSER` | Force admin/non-admin mode |
| `/SP-` | Disable startup prompt |
| `/NOCANCEL` | Disable Cancel button |
| `/CLOSEAPPLICATIONS` / `/NOCLOSEAPPLICATIONS` | Control app closing |
| `/FORCECLOSEAPPLICATIONS` | Force close apps |
| `/RESTARTAPPLICATIONS` | Restart apps after install |

---

## Setup Exit Codes

- `0` — Success
- `1` — Setup failed (internal error)
- `2` — User aborted
- `3` — Fatal error (not installed)
- `4` — Reboot required by user
- `5` — Reboot required without user prompt
- `6` — Reboot required, but /NORESTART was set
- Non-zero custom codes possible via `GetCustomSetupExitCode`

---

## Architecture & 64-bit Notes

- Default `{sys}` maps to 32-bit System32 for 32-bit installers
- Use `{sysnative}` for 64-bit system files
- `ArchitecturesInstallIn64BitMode: x64compatible arm64` to run in 64-bit mode
- Per-entry: `Flags: 32bit` or `64bit` override current install mode
- Registry: `Root: HKLM32` / `HKLM64` to force 32/64-bit registry view
- Check `Is64BitInstallMode()` in Pascal code

---

## .issig File Signatures

Used for file authenticity verification (since IS 6.3).
- Define keys in `[ISSigKeys]` section
- Reference in `[Files]` with `ISSigAllowedKeys`
- Verify downloads with `DownloadTemporaryFileWithISSigVerify`

---

## Useful Patterns

### Version read from EXE at compile time (ISPP)
```iss
#define AppVersion GetFileVersion("MyApp.exe")
[Setup]
AppVersion={#AppVersion}
```

### Conditional entry via Check
```iss
[Files]
Source: "extra.dll"; DestDir: "{app}"; Check: IsFeatureEnabled

[Code]
function IsFeatureEnabled: Boolean;
begin
  Result := WizardIsComponentSelected('extra');
end;
```

### Download file during install
```iss
[Code]
procedure InitializeWizard;
begin
  DownloadPage := CreateDownloadPage(SetupMessage(msgWizardPreparing), SetupMessage(msgPreparingDesc), nil);
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  if CurPageID = wpReady then begin
    DownloadPage.Clear;
    DownloadPage.Add('https://example.com/file.zip', 'file.zip', '');
    DownloadPage.Show;
    try
      DownloadPage.Download;
      Result := True;
    except
      Result := False;
    end;
    DownloadPage.Hide;
  end else
    Result := True;
end;
```

### Custom wizard page
```iss
[Code]
var
  MyPage: TInputQueryWizardPage;

procedure InitializeWizard;
begin
  MyPage := CreateInputQueryPage(wpWelcome, 'Config', 'Enter settings', '');
  MyPage.Add('Server:', False);
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  if CurPageID = MyPage.ID then begin
    if MyPage.Values[0] = '' then begin
      MsgBox('Please enter a server.', mbError, MB_OK);
      Result := False;
      Exit;
    end;
  end;
  Result := True;
end;
```