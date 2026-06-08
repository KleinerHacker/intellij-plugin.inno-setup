# Inno Setup Documentation Coverage Status

> **Reference version:** Inno Setup 7.0.1-beta  
> **Docs URL:** https://jrsoftware.org/ishelp/  
> **Last checked:** 2026-06-06  
>
> This file tracks which parts of the official Inno Setup documentation are implemented in the plugin's
> spec files (`src/main/resources/spec/`) and language support. Update the "Last checked" date and the
> status entries whenever the Inno Setup docs change or the plugin catches up.
>
> **Legend:** ✅ implemented · ⚠️ partial · ❌ missing · 🗑️ obsolete (skip)

---

## Sections

| Section         | Type       | Status | Notes                                        |
|-----------------|------------|--------|----------------------------------------------|
| `[Setup]`       | directive  | ⚠️     | ~48 of ~174 current attributes (see below)   |
| `[Types]`       | parameter  | ✅     | All attributes + flags covered               |
| `[Components]`  | parameter  | ✅     | All attributes + flags covered               |
| `[Tasks]`       | parameter  | ✅     | All attributes + flags covered               |
| `[Dirs]`        | parameter  | ✅     | All attributes + flags covered               |
| `[Files]`       | parameter  | ✅     | All attributes + flags covered               |
| `[Icons]`       | parameter  | ✅     | All attributes + flags covered               |
| `[INI]`         | parameter  | ❌     | Entirely missing from spec                   |
| `[Registry]`    | parameter  | ✅     | All attributes + flags covered               |
| `[Run]`         | parameter  | ✅     | All attributes + flags covered               |
| `[UninstallRun]`| parameter  | ✅     | All attributes + flags covered               |
| `[Languages]`   | parameter  | ✅     | All attributes covered                       |
| `[Messages]`    | directive  | ❌     | Entirely missing from spec                   |
| `[CustomMessages]`| directive| ❌     | Entirely missing from spec                   |
| `[LangOptions]` | directive  | ✅     | All directives covered; TitleFont*/CopyrightFont* removed in 6.4; LanguageID completion |
| `[InstallDelete]`| parameter | ✅     | All attributes covered                       |
| `[UninstallDelete]`| parameter| ✅    | All attributes covered                       |
| `[ISSigKeys]`   | parameter  | ❌     | Entirely missing (added ~IS 6.x)             |
| `[Code]`        | code       | ✅     | Treated as injected Pascal, no spec needed   |

---

## [Setup] Section — Missing Attributes

The spec covers **~48** attributes. The official docs list **~174** current (non-obsolete) attributes.
Grouped below are the attributes **not yet in `isi-spec.yaml`**.

### Compiler / Build
- `ASLRCompatible`, `DEPCompatible`, `TerminalServicesAware`
- `CompressionThreads`, `InternalCompressLevel`, `SolidCompression`
- `LZMAAlgorithm`, `LZMABlockSize`, `LZMADictionarySize`, `LZMAMatchFinder`, `LZMANumBlockThreads`, `LZMANumFastBytes`, `LZMAUseSeparateProcess`
- `DiskSpanning`, `DiskSliceSize`, `DiskClusterSize`, `SlicesPerDisk`
- `MergeDuplicateFiles`, `ReserveBytes`, `UseSetupLdr`
- `Output` (boolean — enable/disable compiler output)
- `OutputManifestFile`
- `DisablePrecompiledFileVerifications`
- `MissingMessagesWarning`, `NotRecognizedMessagesWarning`, `MissingRunOnceIdsWarning`, `UsedUserAreasWarning`

### Signing
- `Encryption` (boolean — pair with `EncryptionKey`)
- `SignTool`, `SignToolMinimumTimeBetween`, `SignToolRetryCount`, `SignToolRetryDelay`, `SignToolRunMinimized`
- `SignedUninstaller`, `SignedUninstallerDir`

### Version Info (EXE properties)
- `VersionInfoCompany`, `VersionInfoCopyright`, `VersionInfoDescription`
- `VersionInfoOriginalFileName`, `VersionInfoProductName`
- `VersionInfoProductVersion`, `VersionInfoProductTextVersion`
- `VersionInfoVersion`, `VersionInfoTextVersion`

### App Identity
- `AppId`, `AppVerName`, `AppCopyright`, `AppComments`, `AppContact`
- `AppModifyPath`, `AppReadmeFile`, `AppSupportPhone`

### Privileges & Architecture
- `Password`, `RedirectionGuard`, `SetupArchitecture`
- `ArchiveExtraction`

### Installation Behaviour
- `AlwaysRestart`, `RestartApplications`, `RestartIfNeededByRun`
- `CloseApplicationsFilter`, `CloseApplicationsFilterExcludes`
- `ChangesAssociations`, `ChangesEnvironment`
- `CreateAppDir`
- `AllowCancelDuringInstall`, `AllowNetworkDrive`, `AllowRootDirectory`, `AllowUNCPath`
- `ExtraDiskSpaceRequired`
- `SetupMutex` (different from `AppMutex`)
- `TouchDate`, `TouchTime`, `TimeStampRounding`, `TimeStampsInUTC`

### Uninstall
- `UninstallDisplaySize`, `UninstallFilesDir`, `UninstallLogMode`
- `UninstallLogging`, `UninstallRestartComputer`, `UpdateUninstallLogAppName`

### Wizard Pages & UI
- `DisableFinishedPage`, `DisableReadyMemo`, `DisableStartupPrompt`
- `EnableDirDoesntExistWarning`, `DirExistsWarning`
- `AlwaysShowComponentsList`, `AlwaysShowDirOnReadyPage`, `AlwaysShowGroupOnReadyPage`
- `AlwaysUsePersonalGroup`, `AppendDefaultDirName`, `AppendDefaultGroupName`
- `ShowLanguageDialog`, `LanguageDetectionMethod`
- `DefaultDialogFontName`
- `UsePreviousLanguage`, `UsePreviousPrivileges`, `UsePreviousSetupType`, `UsePreviousTasks`, `UsePreviousUserInfo`
- `DefaultUserInfoName`, `DefaultUserInfoOrg`, `DefaultUserInfoSerial`
- `FlatComponentsList`, `ShowComponentSizes`, `ShowTasksTreeLines`

### Cosmetic / Wizard Appearance
- `WizardBackColor`, `WizardBackColorDynamicDark`
- `WizardBackImageFile`, `WizardBackImageFileDynamicDark`, `WizardBackImageOpacity`
- `WizardImageAlphaFormat`, `WizardImageBackColor`, `WizardImageBackColorDynamicDark`
- `WizardImageFileDynamicDark`, `WizardImageOpacity`, `WizardImageStretch`
- `WizardImageKeepAspectRatio`, `WizardSizePercent`
- `WizardSmallImageBackColor`, `WizardSmallImageBackColorDynamicDark`, `WizardSmallImageFileDynamicDark`
- `WizardStyleFile`, `WizardStyleFileDynamicDark`

### Obsolete (intentionally skipped)
🗑️ `AlwaysCreateUninstallIcon`, `BackColor`, `BackColor2`, `BackColorDirection`, `BackSolid`,
`DisableAppendDir`, `DontMergeDuplicateFiles`, `MessagesFile`, `UninstallIconFile`,
`UninstallIconName`, `UninstallStyle`, `WindowResizable`, `WindowShowCaption`,
`WindowStartMaximized`, `WindowVisible`, `WizardResizable`

---

## Missing Sections — Details

### `[INI]` section
Allows Setup to modify `.ini` files on the user's system.  
**Attributes:** `Filename` (req), `Section` (req), `Key`, `String`  
**Flags:** `createkeyifdoesntexist`, `uninsdeleteentry`, `uninsdeletesection`, `uninsdeletesectionifempty`  
**Common params:** `Components`, `Tasks`, `Languages`, `Check`, `MinVersion`, `OnlyBelowVersion`

### `[Messages]` section
Overrides installer message strings from `Default.isl`.  
**Format:** `MessageID=Text` or `lang.MessageID=Text` — free key/value, no fixed attribute list.  
**Note:** Only needs lexer/syntax support; a full attribute spec is not meaningful here.

### `[CustomMessages]` section
Defines custom localizable strings usable via the `{cm:…}` constant.  
**Format:** Same as `[Messages]` — free key/value.

### `[LangOptions]` section ✅
Language-specific display settings (used inside `.isl` files and overridable in scripts). Implemented
as a directive section in `isi-spec.yaml`.  
**Attributes:** `LanguageName`, `LanguageID`, `LanguageCodePage`, `DialogFontName`, `DialogFontSize`,
`DialogFontBaseScaleWidth`, `DialogFontBaseScaleHeight`, `WelcomeFontName`, `WelcomeFontSize`, `RightToLeft`  
**Removed in 6.4:** `TitleFontName`, `TitleFontSize`, `CopyrightFontName`, `CopyrightFontSize` (marked `until: "6.4"`)  
**Extras:** `LanguageID` offers a completion popup of Windows LCIDs (name + flag icon + greyed `$hex`
id, from `IssWindowsLanguage`), and is validated against the full MSDN LCID set — a value that is
neither `0` nor a recognised LCID is flagged as a warning. Integer directives also accept Pascal-hex
(`$`-prefixed) values. An inlay hint shows the flag + locale name after `LanguageID=`; `[Languages]`
`Name`/`MessagesFile` show the matching flag before the string (omitted when unknown). See
`IsiLanguageInlayHintsProvider`.

### `[ISSigKeys]` section
Declares public keys for `.issig` file-signature verification (used with `issigverify` flag in `[Files]`).  
**Attributes:** `Name` (req), `KeyFile`, `PublicX`, `PublicY`, `KeyID`, `Group`, `RuntimeID`

---

## Constants (`isi-const.yaml`)

Coverage appears **complete** for the constants documented at the time of last check (58 entries including
deprecated). Notable items already marked deprecated/removed in `isi-const.yaml`:
- `{hwnd}` — removed in Inno Setup 6.4 ✅ annotated
- `{pf}`, `{pf32}`, `{pf64}`, `{cf}`, `{cf32}`, `{cf64}`, `{fonts}`, `{sendto}` — deprecated ✅ annotated

---

## ISPP Preprocessor (`iss-ispp.yaml`)

Coverage appears **complete**: 24 directives, 13+ predefined variables, 31+ builtin functions.
All standard directives (`#define`, `#undef`, `#if`/`#elif`/`#else`/`#endif`, `#ifdef`, `#ifndef`,
`#include`, `#for`, `#sub`, `#endsub`, `#emit`, `#expr`, `#pragma`, `#error`, etc.) are present.

---

## IDE Features

| Feature                          | Status | Notes                                        |
|----------------------------------|--------|----------------------------------------------|
| Syntax highlighting (ISI)        | ✅     |                                              |
| Syntax highlighting (ISPP)       | ✅     |                                              |
| Code completion — sections       | ✅     |                                              |
| Code completion — attributes     | ✅     |                                              |
| Code completion — flags          | ✅     |                                              |
| Code completion — constants      | ✅     |                                              |
| Code completion — ISPP directives| ✅     |                                              |
| Code completion — `[Languages]`  | ✅     | Built-in names + MessagesFile, with flag icons|
| Code completion — `LanguageID`   | ✅     | Windows LCIDs: name + flag + greyed `$hex` id |
| Inlay hints — language flags     | ✅     | Flag before `[Languages]` Name/MessagesFile; flag + locale name on `[LangOptions]` LanguageID|
| Brace matching `[]`, `{}`, `()`  | ✅     |                                              |
| Code folding                     | ✅     |                                              |
| Structure view                   | ✅     |                                              |
| Documentation popup              | ✅     | From spec YAML descriptions                  |
| Find usages                      | ✅     | For ISPP `#define` references                |
| Rename refactoring               | ✅     | For ISPP identifiers                         |
| Commenter (`Ctrl+/`)             | ✅     |                                              |
| Quote handler                    | ✅     |                                              |
| ISPP language injection          | ✅     | Preprocessor lines injected into ISI         |
| Semantic annotations / errors    | ✅     |                                              |
| `[Languages]` Name/file mismatch | ✅     | Warns when Name ≠ built-in `compiler:` MessagesFile|
| [Code] section Pascal support    | ❌     | No Pascal intellisense; treated as plain text|
