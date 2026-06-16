# `#define`

`#define` declares a preprocessor macro — a named value or expression that is substituted into the script
at compile time. It is the most-used ISPP directive and the one the plugin supports with full semantics
(reference resolution, rename and Find Usages).

---

## Syntax

```ini
#define Name [Value]
#define Name(Param1, Param2) Expression
```

- `#define Name Value` defines a constant macro (the value may be omitted, defining a *void* macro).
- `#define Name(params) Expression` defines a function-like macro. The plugin flags a function-like macro
  that has no expression body as an error.
- `#undef Name` removes a previously defined macro.

---

## Using a macro: `{#Name}`

Inside a normal script line, `{#Name}` (short for `{#emit Name}`) emits the macro's value:

```ini
#define MyAppVersion "1.5.0"

[Setup]
AppVersion={#MyAppVersion}
OutputBaseFilename=setup-{#MyAppVersion}
```

- `{#Name}` resolves to its `#define` declaration — go-to-definition (**Ctrl+B** / **Cmd+B**) and
  Find Usages (**Alt+F7**) work, and rename keeps the declaration and all usages in sync.
- It is offered in completion both right after `{` and after `{#`.
- A `#define` that is never used is flagged with a quick-fix to remove it.

---

## Expressions and operations in `#define`

The value of a `#define` is not just a literal — it is a full **expression**. ISPP evaluates it at compile
time using a C/C++-like expression grammar, and the plugin parses, type-checks and highlights it. A single
literal is the trivial case; as soon as you combine several values you must join them with **operators**.

```ini
#define Major     1
#define Minor     5
#define Build     100
#define Version   Str(Major) + "." + Str(Minor)   ; string concatenation
#define NextBuild (Build + 1)                      ; arithmetic, grouped
#define OutputDir "Builds\\" + Version             ; concatenation with another macro
#define IsBeta    Build < 200                       ; comparison → integer 0/1
```

### The type system

Every expression has one of these types; the plugin infers it to validate operations:

| Type | Produced by | Notes |
|------|-------------|-------|
| `int` | integer literals (`100`), arithmetic/comparison/logical results | |
| `str` | string literals (`"x"`, `'x'`), string-returning functions | Single or double quotes; a doubled quote `""` is a literal quote |
| `void` | a `#define` with **no** value | Compatible with both `int` (as `0`) and `str` (as `""`) |
| `any` | unresolved references, macro parameters, unknown function results, `{…}` constants | Suppresses type checking — never causes a false error |

### Operators

ISPP supports the full C/C++-like operator set. The plugin highlights every operator token and applies the
type rules below.

| Group | Operators | Operand types | Result |
|-------|-----------|---------------|--------|
| Arithmetic | `+` `-` `*` `/` `%` | `int` (for `+` also `str` + `str`) | `int` (or `str` for concatenation) |
| String concatenation | `+` | `str` + `str` | `str` |
| Comparison | `<` `>` `<=` `>=` `==` `!=` | both `int` **or** both `str` | `int` (`0`/`1`) |
| Logical | `&&` `\|\|` `!` | `int` | `int` |
| Bitwise | `&` `\|` `^` `~` | `int` | `int` |
| Shift | `<<` `>>` | `int` | `int` |
| Ternary | `cond ? a : b` | condition `int` | type of the branches |
| Unary (prefix) | `+` `-` `~` `!` | `int` | `int` |
| Grouping | `( … )` | — | type of the inner expression |
| Comma | `a , b` | — | type of the right operand |

**Precedence** (highest binds tightest, mirrors C/C++):
`( )` and function calls → unary `+ - ~ !` → `* / %` → `+ -` → `<< >>` → `< > <= >=` → `== !=` →
`&` → `^` → `|` → `&&` → `||` → `?:` → `,`.
Use parentheses when in doubt — `#define X 1 + 2 * 3` is `7`, `#define X (1 + 2) * 3` is `9`.

### Function calls feed the types

A `#define` value may call any ISPP **built-in function** (the full official set is bundled in the plugin),
and the function's return type flows into the surrounding expression — e.g. `Str(...)` is `str`, `Int(...)`
and `Power(...)` are `int`, `FileExists(...)` is `int`. So `Str(Major) + "."` is valid (`str` + `str`),
while `Str(Major) * 2` is rejected.

```ini
#define FullVer  GetFileVersionString("app.exe")      ; str
#define Padded   "v" + Str(Build)                       ; str + str → str
#define Doubled  Power(2, 10) * 2                        ; int * int → int
```

### Recursive reference resolution

A reference to another macro takes **that macro's** type, resolved recursively through the name — so type
errors are caught even when the operands are themselves `#define`s:

```ini
#define A "x"
#define B 5
#define C A * B     ; A is str, B is int → "str * int" is flagged on the *
```

Resolution only follows **backward** references (a macro must already be declared on an earlier line), which
makes reference rings impossible in a well-formed script. A residual cycle (e.g. a self-reference
`#define P P + 1`, or an out-of-order mutual reference) is broken safely: the reference collapses to `any`,
so there is neither an infinite loop nor a false error.

### What the plugin flags as an error

Each problem is reported as an **error** anchored at the precise offending token (not the whole line):

| Example | Marked token | Reason |
|---------|-------------|--------|
| `#define X "a" * "b"` | `*` | arithmetic operator on string operands |
| `#define X 1 + "s"` | `+` | mixing integer and string in `+` |
| `#define X "a" < 1` | `<` | comparing a string with an integer |
| `#define X -"s"` | `"s"` | unary minus on a string operand |
| `#define X 5 6` | `6` | two operands without an operator |
| `#define X (1 + 2` | `(` | unbalanced parenthesis |

An expression that involves an `any` operand (an unresolved reference, a macro parameter, an unknown
function, or a `{…}` constant) is intentionally **not** flagged, to avoid false positives on valid scripts.

### Function-like macro bodies

The expression rules apply to function-like macro bodies too; the parameters are treated as `any`, so they
never trigger type errors:

```ini
#define Max(a, b) a > b ? a : b
#define Clamp(x)  x < 0 ? 0 : x
```

---

## Built-in function reference

ISPP ships a large set of **built-in functions** that you may call inside a `#define` expression. The plugin
bundles the complete official set; each function's return type feeds the expression type checker (see above),
and the functions are offered in completion. The list below is exhaustive and sorted alphabetically (matching
the official ISPP function index).

!!! note "Notation"
    A parameter shown as `Name: int*` / `Name: str*` is passed **by reference** — the function writes back
    into the supplied variable. A trailing `= value` marks an **optional** parameter with a default.

| Function | Returns | Description |
|----------|---------|-------------|
| `AddBackslash(S: str): str` | `str` | Adds a trailing backslash to S if not already present. |
| `AddQuotes(S: str): str` | `str` | Surrounds S with double quotes if it contains spaces. |
| `ChangeFileExt(Filename: str, Extension: str): str` | `str` | Returns Filename with its extension replaced by Extension. |
| `ComparePackedVersion(Version1: int, Version2: int): int` | `int` | Compares two packed (encoded) version numbers; returns -1, 0 or 1. |
| `Copy(S: str, Index: int, Count: int): str` | `str` | Returns a substring of S. Index is 1-based. |
| `CopyFile(ExistingFile: str, NewFile: str): int` | `int` | Copies an existing file at compile time; returns non-zero on success. |
| `DecodeVer(Version: int): str` | `str` | Converts a packed version number into a dotted version string. |
| `Defined(Ident): int` | `int` | Returns 1 if the identifier is defined, 0 otherwise. |
| `Delete(S: str*, Index: int, Count: int)` | `void` | Deletes Count characters from S starting at Index (modifies S by reference). |
| `DeleteFile(Filename: str): int` | `int` | Deletes a file at compile time; returns non-zero on success. |
| `DeleteFileNow(Filename: str): int` | `int` | Deletes a file immediately during preprocessing; returns non-zero on success. |
| `DimOf(Array): int` | `int` | Returns the number of elements of an array variable. |
| `DirExists(Path: str): int` | `int` | Returns 1 if the directory exists, 0 otherwise. |
| `EmitLanguagesSection()` | `void` | Emits a [Languages] section from the bundled language files. |
| `EncodeVer(Major: int, Minor: int, Revision: int = 0, Build: int = 0): int` | `int` | Encodes version components into a single packed version number. |
| `EntryCount(Section: str): int` | `int` | Returns the number of entries in the given script section. |
| `Error(Message: str)` | `void` | Raises a compile-time error with the given message. |
| `Exec(CmdLine: str, Params: str = "", WorkingDir: str = "", ShowCmd: int = 0, Wait: int = 0): int` | `int` | Executes a program at compile time; returns the process exit code. |
| `ExecAndGetFirstLine(CmdLine: str, Params: str = "", WorkingDir: str = ""): str` | `str` | Executes a program and returns the first line of its standard output. |
| `ExtractFileDir(Filename: str): str` | `str` | Returns the directory part of Filename (without a trailing backslash). |
| `ExtractFileExt(Filename: str): str` | `str` | Returns the extension of Filename (including the leading dot). |
| `ExtractFileName(Filename: str): str` | `str` | Returns the name and extension part of Filename. |
| `ExtractFilePath(Filename: str): str` | `str` | Returns the drive and directory part of Filename (with a trailing backslash). |
| `FileClose(Handle: int)` | `void` | Closes a file previously opened with FileOpen. |
| `FileEof(Handle: int): int` | `int` | Returns non-zero when the end of an open file has been reached. |
| `FileExists(Filename: str): int` | `int` | Returns 1 if the file exists, 0 otherwise. |
| `FileOpen(Filename: str): int` | `int` | Opens a text file for reading and returns a file handle. |
| `FileRead(Handle: int): str` | `str` | Reads the next line from an open file. |
| `FileReset(Handle: int)` | `void` | Resets the read position of an open file to the beginning. |
| `FileSize(Filename: str): int` | `int` | Returns the size of a file in bytes. |
| `Find(S: str, Substr: str, Index: int = 1): int` | `int` | Returns the position of Substr in S starting at Index, or 0. |
| `FindClose(Handle: int)` | `void` | Closes a search handle opened with FindFirst. |
| `FindCode(): int` | `int` | Returns the line index where the [Code] section begins. |
| `FindFirst(Pattern: str, Attributes: int = 0): int` | `int` | Starts a file search and returns a handle, or a negative value if none found. |
| `FindGetFileName(Handle: int): str` | `str` | Returns the file name found by the current FindFirst/FindNext. |
| `FindNext(Handle: int): int` | `int` | Advances a file search to the next match; returns non-zero on success. |
| `FindSection(Section: str): int` | `int` | Returns the line index of the given section header. |
| `FindSectionEnd(Section: str): int` | `int` | Returns the line index following the last entry of the given section. |
| `ForceDirectories(Dir: str): int` | `int` | Creates a directory tree at compile time; returns non-zero on success. |
| `GetDateTimeString(Format: str, DateSep: str, TimeSep: str): str` | `str` | Returns the current date/time formatted according to Format. |
| `GetEnv(Name: str): str` | `str` | Returns the value of an environment variable. |
| `GetFileCompanyString(Filename: str): str` | `str` | Returns the CompanyName string from a file's version information. |
| `GetFileCopyrightString(Filename: str): str` | `str` | Returns the LegalCopyright string from a file's version information. |
| `GetFileDateTimeString(Filename: str, Format: str, DateSep: str, TimeSep: str): str` | `str` | Returns a file's last-modified timestamp formatted according to Format. |
| `GetFileDescriptionString(Filename: str): str` | `str` | Returns the FileDescription string from a file's version information. |
| `GetFileOriginalFilenameString(Filename: str): str` | `str` | Returns the OriginalFilename string from a file's version information. |
| `GetFileProductVersionString(Filename: str): str` | `str` | Returns the ProductVersion string from a file's version information. |
| `GetFileVersionString(Filename: str): str` | `str` | Returns the file version of an executable or DLL as a dotted string, e.g. <code>1.2.3.4</code>. |
| `GetMD5OfFile(Filename: str): str` | `str` | Returns the MD5 hash of a file as a hex string. |
| `GetMD5OfString(S: str): str` | `str` | Returns the MD5 hash of an ANSI string as a hex string. |
| `GetMD5OfUnicodeString(S: str): str` | `str` | Returns the MD5 hash of a Unicode string as a hex string. |
| `GetPackedVersion(Filename: str): int` | `int` | Returns the packed (encoded) version number of a file. |
| `GetSHA1OfFile(Filename: str): str` | `str` | Returns the SHA-1 hash of a file as a hex string. |
| `GetSHA1OfString(S: str): str` | `str` | Returns the SHA-1 hash of an ANSI string as a hex string. |
| `GetSHA1OfUnicodeString(S: str): str` | `str` | Returns the SHA-1 hash of a Unicode string as a hex string. |
| `GetSHA256OfFile(Filename: str): str` | `str` | Returns the SHA-256 hash of a file as a hex string. |
| `GetSHA256OfString(S: str): str` | `str` | Returns the SHA-256 hash of an ANSI string as a hex string. |
| `GetSHA256OfUnicodeString(S: str): str` | `str` | Returns the SHA-256 hash of a Unicode string as a hex string. |
| `GetStringFileInfo(Filename: str, Key: str): str` | `str` | Returns a string from the version info of a file. Common keys: <code>FileVersion</code>, <code>ProductVersion</code>, <code>CompanyName</code>. |
| `GetVersionComponents(Filename: str, Major: int*, Minor: int*, Revision: int*, Build: int*): int` | `int` | Reads a file's version components into the referenced variables; returns non-zero on success. |
| `GetVersionNumbers(Filename: str, VersionMS: int*, VersionLS: int*): int` | `int` | Reads a file's version into the referenced high/low words; returns non-zero on success. |
| `GetVersionNumbersString(Filename: str): str` | `str` | Returns a file's version as a dotted string, e.g. <code>1.2.3.4</code>. |
| `Insert(Source: str, S: str*, Index: int)` | `void` | Inserts Source into S at Index (modifies S by reference). |
| `Int(Value: any, Default: int = 0): int` | `int` | Converts a value to integer, using Default when conversion fails. |
| `Is64BitPEImage(Filename: str): int` | `int` | Returns non-zero if the given PE image is 64-bit. |
| `IsWin64(): int` | `int` | Returns non-zero when the compiler is running on 64-bit Windows. |
| `Len(S: str): int` | `int` | Returns the length of the string. |
| `LowerCase(S: str): str` | `str` | Returns the string converted to lower case. |
| `Max(A: int, B: int): int` | `int` | Returns the greater of two integers. |
| `Message(S: str)` | `void` | Outputs an informational message in the compiler log. |
| `Min(A: int, B: int): int` | `int` | Returns the smaller of two integers. |
| `PackVersionComponents(Major: int, Minor: int, Revision: int, Build: int): int` | `int` | Packs version components into a single packed version number. |
| `PackVersionNumbers(VersionMS: int, VersionLS: int): int` | `int` | Packs high/low version words into a single packed version number. |
| `Pos(Substr: str, S: str): int` | `int` | Returns the 1-based position of Substr in S, or 0 if not found. |
| `Power(Base: int, Exponent: int): int` | `int` | Returns Base raised to the power of Exponent. |
| `ReadIni(Filename: str, Section: str, Key: str, Default: str = ""): str` | `str` | Reads a value from an INI file at compile time. |
| `ReadReg(RootKey: int, SubKeyName: str, ValueName: str = "", Default: str = ""): str` | `str` | Reads a registry value at compile time. |
| `RemoveBackslashUnlessRoot(S: str): str` | `str` | Removes a trailing backslash from S unless S is a drive root. |
| `RemoveFileExt(Filename: str): str` | `str` | Returns Filename with its extension removed. |
| `RPos(Substr: str, S: str): int` | `int` | Returns the 1-based position of the last occurrence of Substr in S, or 0. |
| `SamePackedVersion(Version1: int, Version2: int): int` | `int` | Returns non-zero if two packed version numbers are equal. |
| `SameStr(S1: str, S2: str): int` | `int` | Returns non-zero if two strings are equal (case-insensitive). |
| `SameText(S1: str, S2: str): int` | `int` | Returns non-zero if two strings are equal (case-insensitive). |
| `SaveStringToFile(Filename: str, S: str, Append: int = 0)` | `void` | Writes a string to a file, optionally appending. |
| `SaveToFile(Filename: str)` | `void` | Writes the preprocessed output collected so far to a file (debugging aid). |
| `SetSetupSetting(Name: str, Value: str)` | `void` | Sets a [Setup] section directive at compile time. |
| `SetupSetting(Name: str): str` | `str` | Returns the value of a [Setup] section directive by name. |
| `Str(Value: any): str` | `str` | Converts a value to string. Integers become text, void becomes an empty string. |
| `StringChange(S: str*, FromStr: str, ToStr: str): int` | `int` | Replaces all occurrences of FromStr with ToStr in S; returns the replacement count. |
| `StrToVersion(S: str): int` | `int` | Parses a dotted version string into a packed version number. |
| `Trim(S: str): str` | `str` | Returns S with leading and trailing whitespace removed. |
| `TypeOf(Ident): int` | `int` | Returns the type of an identifier: 0=void, 1=int, 2=str. |
| `UnpackVersionComponents(Version: int, Major: int*, Minor: int*, Revision: int*, Build: int*)` | `void` | Splits a packed version number into the referenced components. |
| `UnpackVersionNumbers(Version: int, VersionMS: int*, VersionLS: int*)` | `void` | Splits a packed version number into the referenced high/low words. |
| `UpperCase(S: str): str` | `str` | Returns the string converted to upper case. |
| `VersionToStr(Version: int): str` | `str` | Converts a packed version number into a dotted version string. |
| `Warning(Message: str)` | `void` | Emits a compile-time warning with the given message. |
| `WriteIni(Filename: str, Section: str, Key: str, Value: str)` | `void` | Writes a value to an INI file at compile time. |
| `YesNo(S: str): int` | `int` | Returns non-zero if the string represents an affirmative (yes/true) value. |

---

## Predefined variables

Besides your own `#define`s, ISPP provides a set of **predefined variables** that are available without
declaring them. The **value-bearing** ones (`int` / `str`) are emitted inline with `{#…}` exactly like a
user define and may be used in expressions; the **valueless** ones (`void`) exist only for conditional
compilation. The list below is complete:

| Variable | Type | Description |
|----------|------|-------------|
| `__COUNTER__` | `int` | Auto-incrementing counter; increments each time it is used. |
| `__LINE__` | `int` | The current line number in the current file. |
| `__FILENAME__` | `str` | The filename portion of the current include file path. |
| `__PATHFILENAME__` | `str` | The full path of the current include file. |
| `__DIR__` | `str` | The directory portion of the current include file path. |
| `__INCLUDE__` | `str` | The current include path (multiple paths separated by semicolons). |
| `__WIN32__` | `void` | Always defined. Can be used with #ifdef to detect ISPP environment. |
| `ISPP_INVOKED` | `void` | Always defined when ISPP is active. |
| `ISCC_INVOKED` | `void` | Defined when compilation uses the console-mode compiler (ISCC.exe). |
| `PREPROCVER` | `int` | 32-bit packed version number of the Inno Setup preprocessor. |
| `Ver` | `int` | Alias for PREPROCVER. |
| `WINDOWS` | `void` | Always defined. |
| `UNICODE` | `void` | Always defined (ISPP is Unicode-only). |
| `CompilerPath` | `str` | The directory where the Inno Setup compiler (ISCC.exe) is located. |
| `SourcePath` | `str` | The directory containing the root script file. |
| `SysPath` | `str` | The system directory appropriate to the compiler type. |
| `NewLine` | `str` | The newline character sequence. |
| `Tab` | `str` | The tab character. |

These appear in `{#…}` completion and are accepted by validation. The path-relevant ones
(`{#SourcePath}`, `{#__DIR__}`, `{#CompilerPath}`, `{#SysPath}`) are also expanded when the plugin resolves
a `[Languages]` `MessagesFile` path; the remaining, dynamic ones are left unresolved rather than producing
a false error.

!!! note "Valueless symbols"
    `__WIN32__`, `ISPP_INVOKED`, `ISCC_INVOKED`, `WINDOWS` and `UNICODE` carry **no value** (type `void`):
    they are only *defined* for conditional compilation (`#ifdef` / `#if defined(...)`) and therefore
    **cannot** be emitted via `{#…}`. They are excluded from `{#…}` completion and are not accepted as
    inline emissions.
