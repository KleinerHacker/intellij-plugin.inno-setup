# [Files]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=filessection){ .md-button .md-button--primary }

The `[Files]` section is where you declare every file your installer should copy to the target machine. It supports
wildcards, recursive directory trees, font installation, .NET GAC registration, file integrity hashing, and even
downloading files from the internet at install time. Directories needed by the listed files are created automatically —
no `[Dirs]` entries required.

---

## Source

`string` · **Required**

Source file path or wildcard pattern, e.g. `MyProg.exe` or `Plugins\*`. Relative to `SourceDir`.

---

## DestDir

`string` · **Required**

Destination directory on the target machine, e.g. `{app}` or `{sys}`. Supports all Inno Setup constants.

---

## DestName

`string`

Rename the file on the target machine. If omitted, the original filename is kept.

---

## Excludes

`string`

Comma-separated list of filename patterns to exclude when using wildcards, e.g. `*.pdb,*.log`.

---

## ExternalSize

`integer`

Size in bytes of an external file (used with the `external` flag) for accurate disk-space calculation on the wizard
page.

---

## Attribs

`string` · **Multiple values**

File-system attributes to set after the file is installed: `readonly`, `hidden`, `system`, `notcontentindexed`.

---

## Permissions

`string` · **Multiple values**

ACL permissions to set on the installed file, e.g. `users-modify`, `everyone-readexec`.

---

## FontInstall

`string`

Registry font name used when installing a font file, e.g. `My Font (TrueType)`. Triggers font registration in Windows.

---

## StrongAssemblyName

`string`

.NET strong assembly name for Global Assembly Cache (GAC) registration.

---

## Hash

`string` · **Since 6.5**

Expected SHA-256 hash of the source file. Inno Setup verifies the hash at compile time to catch accidental file
corruption.

---

## ISSigAllowedKeys

`string` · **Multiple values** · **Since 6.5**

Comma-separated list of key identifiers from `[ISSigKeys]` used to verify the file's `.issig` signature.

---

## ExtractArchivePassword

`string` · **Since 6.5**

Password for an encrypted archive (used with the `extractarchive` flag). Stored unencrypted inside the installer.

---

## DownloadISSigSource

`string` · **Since 6.5**

URL of the `.issig` signature file for a file downloaded at install time.

---

## DownloadUserName

`string` · **Since 6.5**

HTTP Basic Auth username for authenticated file downloads (requires the `download` flag).

---

## DownloadPassword

`string` · **Since 6.5**

HTTP Basic Auth password for authenticated file downloads (requires the `download` flag).

---

## Flags

`string` · **Multiple values**

Behavioural flags: `32bit`, `64bit`, `comparetimestamp`, `confirmoverwrite`, `deleteafterinstall`, `dontcopy`,
`download`, `external`, `extractarchive`, `ignoreversion`, `isreadme`, `nocompression`, `onlyifdoesntexist`,
`recursesubdirs`, `createallsubdirs`, `regserver`, `regtypelib`, `restartreplace`, `sharedfile`.

---

## Components

`→ Components` · **Multiple values**

This entry is processed only when at least one of the listed components is selected.

---

## Tasks

`→ Tasks` · **Multiple values**

This entry is processed only when at least one of the listed tasks is checked.

---

## Languages

`→ Languages` · **Multiple values**

Limits this entry to the specified languages.

---

## Check

`string`

Name of a Pascal function in `[Code]` that returns `Boolean`. The entry is processed only when the function returns
`True`.

---

## BeforeInstall

`string`

Name of a Pascal procedure in `[Code]` called immediately before this file is installed.

---

## AfterInstall

`string`

Name of a Pascal procedure in `[Code]` called immediately after this file is installed.

---

## MinVersion

`string`

Minimum Windows version for which this entry applies. Use `0` to never apply.

---

## OnlyBelowVersion

`string`

Maximum Windows version (exclusive) for which this entry applies. Use `0` for no upper limit.
