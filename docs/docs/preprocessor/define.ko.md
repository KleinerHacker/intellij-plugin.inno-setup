# `#define`

`#define`은 전처리기 매크로 — 컴파일 시점에 스크립트로 치환되는 명명된 값 또는 표현식 — 를 선언합니다. 가장 많이 사용되는 ISPP 지시문이며, 플러그인이 완전한 의미 체계(참조 해석, 이름 바꾸기, 사용 찾기)로 지원하는 지시문입니다.

---

## 구문

```ini
#define Name [Value]
#define Name(Param1, Param2) Expression
```

- `#define Name Value`는 상수 매크로를 정의합니다(값을 생략하면 *void* 매크로가 됩니다).
- `#define Name(params) Expression`은 함수형 매크로를 정의합니다. 플러그인은 표현식 본문이 없는 함수형 매크로를 오류로 표시합니다.
- `#undef Name`은 이전에 정의된 매크로를 제거합니다.

---

## 매크로 사용: `{#Name}`

일반 스크립트 줄 안에서 `{#Name}`(`{#emit Name}`의 축약형)은 매크로의 값을 출력합니다:

```ini
#define MyAppVersion "1.5.0"

[Setup]
AppVersion={#MyAppVersion}
OutputBaseFilename=setup-{#MyAppVersion}
```

- `{#Name}`은 해당 `#define` 선언으로 해석됩니다 — 정의로 이동(**Ctrl+B** / **Cmd+B**)과 사용 찾기(**Alt+F7**)가 작동하며, 이름 바꾸기는 선언과 모든 사용을 동기화합니다.
- `{` 바로 뒤와 `{#` 뒤 모두에서 완성으로 제공됩니다.
- 한 번도 사용되지 않은 `#define`은 표시되며 제거 빠른 수정이 제공됩니다.

---

## `#define`의 식과 연산

`#define`의 값은 단순한 리터럴이 아니라 완전한 **식**입니다. ISPP는 C/C++ 스타일의 식 문법을 사용하여 컴파일 시점에 평가하며, 플러그인은 이를 파싱·타입 검사·강조 표시합니다. 단일 리터럴은 가장 단순한 경우이며, 여러 값을 결합하려면 **연산자**로 연결해야 합니다.

```ini
#define Major     1
#define Minor     5
#define Build     100
#define Version   Str(Major) + "." + Str(Minor)   ; 문자열 연결
#define NextBuild (Build + 1)                      ; 산술, 괄호로 그룹화
#define OutputDir "Builds\\" + Version             ; 다른 매크로와의 연결
#define IsBeta    Build < 200                       ; 비교 → 정수 0/1
```

### 타입 시스템

모든 식은 다음 타입 중 하나를 가지며, 플러그인은 연산을 검증하기 위해 이를 추론합니다:

| 타입 | 생성 출처 | 비고 |
|------|-----------|------|
| `int` | 정수 리터럴(`100`), 산술/비교/논리 결과 | |
| `str` | 문자열 리터럴(`"x"`, `'x'`), 문자열을 반환하는 함수 | 작은따옴표 또는 큰따옴표. 이중화된 따옴표 `""`는 리터럴 따옴표 |
| `void` | **값이 없는** `#define` | `int`(`0`으로)와 `str`(`""`로) 모두와 호환 |
| `any` | 미해결 참조, 매크로 매개변수, 알 수 없는 함수 결과, `{…}` 상수 | 타입 검사를 억제 — 잘못된 오류를 발생시키지 않음 |

### 연산자

ISPP는 C/C++ 스타일의 전체 연산자 집합을 지원합니다. 플러그인은 모든 연산자 토큰을 강조 표시하고 아래의 타입 규칙을 적용합니다.

| 그룹 | 연산자 | 피연산자 타입 | 결과 |
|------|--------|---------------|------|
| 산술 | `+` `-` `*` `/` `%` | `int`(`+`의 경우 `str` + `str`도 가능) | `int`(연결의 경우 `str`) |
| 문자열 연결 | `+` | `str` + `str` | `str` |
| 비교 | `<` `>` `<=` `>=` `==` `!=` | 둘 다 `int` **또는** 둘 다 `str` | `int`(`0`/`1`) |
| 논리 | `&&` `\|\|` `!` | `int` | `int` |
| 비트 | `&` `\|` `^` `~` | `int` | `int` |
| 시프트 | `<<` `>>` | `int` | `int` |
| 삼항 | `cond ? a : b` | 조건은 `int` | 분기의 타입 |
| 단항(접두) | `+` `-` `~` `!` | `int` | `int` |
| 그룹화 | `( … )` | — | 내부 식의 타입 |
| 쉼표 | `a , b` | — | 오른쪽 피연산자의 타입 |

**우선순위**(높을수록 강하게 결합, C/C++를 따름):
`( )`와 함수 호출 → 단항 `+ - ~ !` → `* / %` → `+ -` → `<< >>` → `< > <= >=` → `== !=` →
`&` → `^` → `|` → `&&` → `||` → `?:` → `,`.
확실하지 않으면 괄호를 사용하세요 — `#define X 1 + 2 * 3`은 `7`, `#define X (1 + 2) * 3`은 `9`입니다.

### 함수 호출이 타입을 공급합니다

`#define` 값은 임의의 ISPP **내장 함수**를 호출할 수 있으며(공식 전체 집합이 플러그인에 포함되어 있습니다), 함수의 반환 타입이 주변 식으로 흘러 들어갑니다 — 예를 들어 `Str(...)`은 `str`, `Int(...)`와 `Power(...)`는 `int`, `FileExists(...)`는 `int`입니다. 따라서 `Str(Major) + "."`은 유효하지만(`str` + `str`), `Str(Major) * 2`는 거부됩니다.

```ini
#define FullVer  GetFileVersionString("app.exe")      ; str
#define Padded   "v" + Str(Build)                       ; str + str → str
#define Doubled  Power(2, 10) * 2                        ; int * int → int
```

### 재귀적 참조 해결

다른 매크로에 대한 참조는 **그 매크로의** 타입을 가지며 이름을 통해 재귀적으로 해결됩니다 — 따라서 피연산자 자체가 `#define`이더라도 타입 오류가 감지됩니다:

```ini
#define A "x"
#define B 5
#define C A * B     ; A는 str, B는 int → "str * int"가 *에 표시됨
```

해결은 **뒤쪽** 참조만 따릅니다(매크로는 이전 줄에 이미 선언되어 있어야 함). 이로써 올바르게 작성된 스크립트에서는 참조 순환이 불가능합니다. 잔여 순환(예: 자기 참조 `#define P P + 1` 또는 순서가 어긋난 상호 참조)은 안전하게 끊어집니다: 참조가 `any`로 축소되므로 무한 루프도 잘못된 오류도 발생하지 않습니다.

### 플러그인이 오류로 표시하는 것

각 문제는 줄 전체가 아니라 **정확한 원인 토큰**을 가리키는 **오류**로 보고됩니다:

| 예 | 표시되는 토큰 | 이유 |
|----|---------------|------|
| `#define X "a" * "b"` | `*` | 문자열 피연산자에 대한 산술 연산자 |
| `#define X 1 + "s"` | `+` | `+`에서 정수와 문자열의 혼합 |
| `#define X "a" < 1` | `<` | 문자열과 정수의 비교 |
| `#define X -"s"` | `"s"` | 문자열 피연산자에 대한 단항 마이너스 |
| `#define X 5 6` | `6` | 연산자 없는 두 피연산자 |
| `#define X (1 + 2` | `(` | 괄호 불균형 |

`any` 피연산자(미해결 참조, 매크로 매개변수, 알 수 없는 함수, `{…}` 상수)를 포함하는 식은 유효한 스크립트에서의 오탐을 피하기 위해 의도적으로 표시되지 **않습니다**.

### 함수형 매크로 본문

식 규칙은 함수형 매크로 본문에도 적용됩니다. 매개변수는 `any`로 처리되므로 타입 오류를 일으키지 않습니다:

```ini
#define Max(a, b) a > b ? a : b
#define Clamp(x)  x < 0 ? 0 : x
```

---

## 내장 함수 참조

ISPP는 `#define` 식 안에서 호출할 수 있는 많은 **내장 함수**를 제공합니다. 플러그인에는 공식 전체 집합이 포함되어 있으며, 각 함수의 반환 형식은 식 형식 검사기에 공급되고(위 참조) 완성에도 제시됩니다. 아래 목록은 빠짐없으며 (공식 ISPP 함수 인덱스에 맞춰) 알파벳순으로 정렬되어 있습니다.

!!! note "표기법"
    `Name: int*` / `Name: str*`로 표시된 매개변수는 **참조로 전달**되며, 함수는 전달된 변수에 다시 씁니다. 끝의 `= value`는 기본값이 있는 **선택적** 매개변수를 나타냅니다.

| 함수 | 반환 | 설명 |
|------|------|------|
| `AddBackslash(S: str): str` | `str` | S의 끝에 백슬래시가 없으면 추가합니다. |
| `AddQuotes(S: str): str` | `str` | S에 공백이 포함되어 있으면 큰따옴표로 감쌉니다. |
| `ChangeFileExt(Filename: str, Extension: str): str` | `str` | Filename의 확장자를 Extension으로 바꿔 반환합니다. |
| `ComparePackedVersion(Version1: int, Version2: int): int` | `int` | 두 패킹된(인코딩된) 버전 번호를 비교하여 -1, 0 또는 1을 반환합니다. |
| `Copy(S: str, Index: int, Count: int): str` | `str` | S의 부분 문자열을 반환합니다. Index는 1부터 시작합니다. |
| `CopyFile(ExistingFile: str, NewFile: str): int` | `int` | 컴파일 시점에 기존 파일을 복사합니다. 성공 시 0이 아닌 값을 반환합니다. |
| `DecodeVer(Version: int): str` | `str` | 패킹된 버전 번호를 점으로 구분된 버전 문자열로 변환합니다. |
| `Defined(Ident): int` | `int` | 식별자가 정의되어 있으면 1, 아니면 0을 반환합니다. |
| `Delete(S: str*, Index: int, Count: int)` | `void` | S의 Index부터 Count개의 문자를 삭제합니다(S를 참조로 수정). |
| `DeleteFile(Filename: str): int` | `int` | 컴파일 시점에 파일을 삭제합니다. 성공 시 0이 아닌 값을 반환합니다. |
| `DeleteFileNow(Filename: str): int` | `int` | 전처리 중에 즉시 파일을 삭제합니다. 성공 시 0이 아닌 값을 반환합니다. |
| `DimOf(Array): int` | `int` | 배열 변수의 요소 개수를 반환합니다. |
| `DirExists(Path: str): int` | `int` | 디렉터리가 존재하면 1, 아니면 0을 반환합니다. |
| `EmitLanguagesSection()` | `void` | 번들된 언어 파일에서 [Languages] 섹션을 출력합니다. |
| `EncodeVer(Major: int, Minor: int, Revision: int = 0, Build: int = 0): int` | `int` | 버전 구성 요소를 하나의 패킹된 버전 번호로 인코딩합니다. |
| `EntryCount(Section: str): int` | `int` | 지정한 스크립트 섹션의 항목 수를 반환합니다. |
| `Error(Message: str)` | `void` | 지정한 메시지로 컴파일 시점 오류를 발생시킵니다. |
| `Exec(CmdLine: str, Params: str = "", WorkingDir: str = "", ShowCmd: int = 0, Wait: int = 0): int` | `int` | 컴파일 시점에 프로그램을 실행하고 프로세스 종료 코드를 반환합니다. |
| `ExecAndGetFirstLine(CmdLine: str, Params: str = "", WorkingDir: str = ""): str` | `str` | 프로그램을 실행하고 표준 출력의 첫 번째 줄을 반환합니다. |
| `ExtractFileDir(Filename: str): str` | `str` | Filename의 디렉터리 부분을 반환합니다(끝 백슬래시 없음). |
| `ExtractFileExt(Filename: str): str` | `str` | Filename의 확장자를 반환합니다(선행 점 포함). |
| `ExtractFileName(Filename: str): str` | `str` | Filename의 이름과 확장자 부분을 반환합니다. |
| `ExtractFilePath(Filename: str): str` | `str` | Filename의 드라이브와 디렉터리 부분을 반환합니다(끝 백슬래시 포함). |
| `FileClose(Handle: int)` | `void` | FileOpen으로 연 파일을 닫습니다. |
| `FileEof(Handle: int): int` | `int` | 열린 파일의 끝에 도달하면 0이 아닌 값을 반환합니다. |
| `FileExists(Filename: str): int` | `int` | 파일이 존재하면 1, 아니면 0을 반환합니다. |
| `FileOpen(Filename: str): int` | `int` | 텍스트 파일을 읽기용으로 열고 파일 핸들을 반환합니다. |
| `FileRead(Handle: int): str` | `str` | 열린 파일에서 다음 줄을 읽습니다. |
| `FileReset(Handle: int)` | `void` | 열린 파일의 읽기 위치를 처음으로 되돌립니다. |
| `FileSize(Filename: str): int` | `int` | 파일 크기를 바이트 단위로 반환합니다. |
| `Find(S: str, Substr: str, Index: int = 1): int` | `int` | Index부터 시작하여 S에서 Substr의 위치를 반환하거나 없으면 0을 반환합니다. |
| `FindClose(Handle: int)` | `void` | FindFirst로 연 검색 핸들을 닫습니다. |
| `FindCode(): int` | `int` | [Code] 섹션이 시작되는 줄 인덱스를 반환합니다. |
| `FindFirst(Pattern: str, Attributes: int = 0): int` | `int` | 파일 검색을 시작하고 핸들을 반환하거나, 없으면 음수를 반환합니다. |
| `FindGetFileName(Handle: int): str` | `str` | 현재 FindFirst/FindNext로 찾은 파일 이름을 반환합니다. |
| `FindNext(Handle: int): int` | `int` | 파일 검색을 다음 일치로 진행합니다. 성공 시 0이 아닌 값을 반환합니다. |
| `FindSection(Section: str): int` | `int` | 지정한 섹션 헤더의 줄 인덱스를 반환합니다. |
| `FindSectionEnd(Section: str): int` | `int` | 지정한 섹션의 마지막 항목 다음 줄 인덱스를 반환합니다. |
| `ForceDirectories(Dir: str): int` | `int` | 컴파일 시점에 디렉터리 트리를 생성합니다. 성공 시 0이 아닌 값을 반환합니다. |
| `GetDateTimeString(Format: str, DateSep: str, TimeSep: str): str` | `str` | 현재 날짜/시간을 Format에 따라 형식화하여 반환합니다. |
| `GetEnv(Name: str): str` | `str` | 환경 변수의 값을 반환합니다. |
| `GetFileCompanyString(Filename: str): str` | `str` | 파일의 버전 정보에서 CompanyName 문자열을 반환합니다. |
| `GetFileCopyrightString(Filename: str): str` | `str` | 파일의 버전 정보에서 LegalCopyright 문자열을 반환합니다. |
| `GetFileDateTimeString(Filename: str, Format: str, DateSep: str, TimeSep: str): str` | `str` | 파일의 마지막 수정 시각을 Format에 따라 형식화하여 반환합니다. |
| `GetFileDescriptionString(Filename: str): str` | `str` | 파일의 버전 정보에서 FileDescription 문자열을 반환합니다. |
| `GetFileOriginalFilenameString(Filename: str): str` | `str` | 파일의 버전 정보에서 OriginalFilename 문자열을 반환합니다. |
| `GetFileProductVersionString(Filename: str): str` | `str` | 파일의 버전 정보에서 ProductVersion 문자열을 반환합니다. |
| `GetFileVersionString(Filename: str): str` | `str` | 실행 파일이나 DLL의 파일 버전을 점으로 구분된 문자열로 반환합니다(예: <code>1.2.3.4</code>). |
| `GetMD5OfFile(Filename: str): str` | `str` | 파일의 MD5 해시를 16진수 문자열로 반환합니다. |
| `GetMD5OfString(S: str): str` | `str` | ANSI 문자열의 MD5 해시를 16진수 문자열로 반환합니다. |
| `GetMD5OfUnicodeString(S: str): str` | `str` | 유니코드 문자열의 MD5 해시를 16진수 문자열로 반환합니다. |
| `GetPackedVersion(Filename: str): int` | `int` | 파일의 패킹된(인코딩된) 버전 번호를 반환합니다. |
| `GetSHA1OfFile(Filename: str): str` | `str` | 파일의 SHA-1 해시를 16진수 문자열로 반환합니다. |
| `GetSHA1OfString(S: str): str` | `str` | ANSI 문자열의 SHA-1 해시를 16진수 문자열로 반환합니다. |
| `GetSHA1OfUnicodeString(S: str): str` | `str` | 유니코드 문자열의 SHA-1 해시를 16진수 문자열로 반환합니다. |
| `GetSHA256OfFile(Filename: str): str` | `str` | 파일의 SHA-256 해시를 16진수 문자열로 반환합니다. |
| `GetSHA256OfString(S: str): str` | `str` | ANSI 문자열의 SHA-256 해시를 16진수 문자열로 반환합니다. |
| `GetSHA256OfUnicodeString(S: str): str` | `str` | 유니코드 문자열의 SHA-256 해시를 16진수 문자열로 반환합니다. |
| `GetStringFileInfo(Filename: str, Key: str): str` | `str` | 파일의 버전 정보에서 문자열을 반환합니다. 일반적인 키: <code>FileVersion</code>, <code>ProductVersion</code>, <code>CompanyName</code>. |
| `GetVersionComponents(Filename: str, Major: int*, Minor: int*, Revision: int*, Build: int*): int` | `int` | 파일의 버전 구성 요소를 참조 변수에 읽어들입니다. 성공 시 0이 아닌 값을 반환합니다. |
| `GetVersionNumbers(Filename: str, VersionMS: int*, VersionLS: int*): int` | `int` | 파일의 버전을 참조하는 상위/하위 워드에 읽어들입니다. 성공 시 0이 아닌 값을 반환합니다. |
| `GetVersionNumbersString(Filename: str): str` | `str` | 파일의 버전을 점으로 구분된 문자열로 반환합니다(예: <code>1.2.3.4</code>). |
| `Insert(Source: str, S: str*, Index: int)` | `void` | Source를 S의 Index에 삽입합니다(S를 참조로 수정). |
| `Int(Value: any, Default: int = 0): int` | `int` | 값을 정수로 변환하며, 변환에 실패하면 Default를 사용합니다. |
| `Is64BitPEImage(Filename: str): int` | `int` | 지정한 PE 이미지가 64비트이면 0이 아닌 값을 반환합니다. |
| `IsWin64(): int` | `int` | 컴파일러가 64비트 Windows에서 실행 중이면 0이 아닌 값을 반환합니다. |
| `Len(S: str): int` | `int` | 문자열의 길이를 반환합니다. |
| `LowerCase(S: str): str` | `str` | 문자열을 소문자로 변환하여 반환합니다. |
| `Max(A: int, B: int): int` | `int` | 두 정수 중 큰 값을 반환합니다. |
| `Message(S: str)` | `void` | 컴파일러 로그에 정보 메시지를 출력합니다. |
| `Min(A: int, B: int): int` | `int` | 두 정수 중 작은 값을 반환합니다. |
| `PackVersionComponents(Major: int, Minor: int, Revision: int, Build: int): int` | `int` | 버전 구성 요소를 하나의 패킹된 버전 번호로 묶습니다. |
| `PackVersionNumbers(VersionMS: int, VersionLS: int): int` | `int` | 상위/하위 버전 워드를 하나의 패킹된 버전 번호로 묶습니다. |
| `Pos(Substr: str, S: str): int` | `int` | S에서 Substr의 1부터 시작하는 위치를 반환하거나 없으면 0을 반환합니다. |
| `Power(Base: int, Exponent: int): int` | `int` | Base의 Exponent 제곱을 반환합니다. |
| `ReadIni(Filename: str, Section: str, Key: str, Default: str = ""): str` | `str` | 컴파일 시점에 INI 파일에서 값을 읽습니다. |
| `ReadReg(RootKey: int, SubKeyName: str, ValueName: str = "", Default: str = ""): str` | `str` | 컴파일 시점에 레지스트리 값을 읽습니다. |
| `RemoveBackslashUnlessRoot(S: str): str` | `str` | S가 드라이브 루트가 아닌 한 끝의 백슬래시를 제거합니다. |
| `RemoveFileExt(Filename: str): str` | `str` | Filename에서 확장자를 제거하여 반환합니다. |
| `RPos(Substr: str, S: str): int` | `int` | S에서 Substr의 마지막 출현 위치를 1부터 시작하는 값으로 반환하거나 없으면 0을 반환합니다. |
| `SamePackedVersion(Version1: int, Version2: int): int` | `int` | 두 패킹된 버전 번호가 같으면 0이 아닌 값을 반환합니다. |
| `SameStr(S1: str, S2: str): int` | `int` | 두 문자열이 같으면(대소문자 무시) 0이 아닌 값을 반환합니다. |
| `SameText(S1: str, S2: str): int` | `int` | 두 문자열이 같으면(대소문자 무시) 0이 아닌 값을 반환합니다. |
| `SaveStringToFile(Filename: str, S: str, Append: int = 0)` | `void` | 문자열을 파일에 쓰며, 선택적으로 추가합니다. |
| `SaveToFile(Filename: str)` | `void` | 지금까지 수집한 전처리 출력을 파일에 씁니다(디버깅용). |
| `SetSetupSetting(Name: str, Value: str)` | `void` | 컴파일 시점에 [Setup] 섹션 지시문을 설정합니다. |
| `SetupSetting(Name: str): str` | `str` | 이름으로 [Setup] 섹션 지시문의 값을 반환합니다. |
| `Str(Value: any): str` | `str` | 값을 문자열로 변환합니다. 정수는 텍스트가 되고 void는 빈 문자열이 됩니다. |
| `StringChange(S: str*, FromStr: str, ToStr: str): int` | `int` | S에서 FromStr을 모두 ToStr로 바꿉니다. 바꾼 횟수를 반환합니다. |
| `StrToVersion(S: str): int` | `int` | 점으로 구분된 버전 문자열을 패킹된 버전 번호로 파싱합니다. |
| `Trim(S: str): str` | `str` | S의 앞뒤 공백을 제거하여 반환합니다. |
| `TypeOf(Ident): int` | `int` | 식별자의 형식을 반환합니다: 0=void, 1=int, 2=str. |
| `UnpackVersionComponents(Version: int, Major: int*, Minor: int*, Revision: int*, Build: int*)` | `void` | 패킹된 버전 번호를 참조 구성 요소로 분할합니다. |
| `UnpackVersionNumbers(Version: int, VersionMS: int*, VersionLS: int*)` | `void` | 패킹된 버전 번호를 참조하는 상위/하위 워드로 분할합니다. |
| `UpperCase(S: str): str` | `str` | 문자열을 대문자로 변환하여 반환합니다. |
| `VersionToStr(Version: int): str` | `str` | 패킹된 버전 번호를 점으로 구분된 버전 문자열로 변환합니다. |
| `Warning(Message: str)` | `void` | 지정한 메시지로 컴파일 시점 경고를 발행합니다. |
| `WriteIni(Filename: str, Section: str, Key: str, Value: str)` | `void` | 컴파일 시점에 INI 파일에 값을 씁니다. |
| `YesNo(S: str): int` | `int` | 문자열이 긍정(yes/true)을 나타내면 0이 아닌 값을 반환합니다. |

---

## 사전 정의 변수

직접 만든 `#define` 외에도 ISPP는 선언 없이 사용할 수 있는 **사전 정의 변수** 집합을 제공합니다. **값을 가진** 변수(`int` / `str`)는 사용자 정의와 마찬가지로 `{#…}`로 인라인 출력할 수 있으며 식에서도 사용할 수 있습니다. **값이 없는** 변수(`void`)는 조건부 컴파일 전용입니다. 아래 목록은 완전합니다:

| 변수 | 형식 | 설명 |
|------|------|------|
| `__COUNTER__` | `int` | 자동 증가 카운터. 사용할 때마다 증가합니다. |
| `__LINE__` | `int` | 현재 파일의 현재 줄 번호. |
| `__FILENAME__` | `str` | 현재 include 파일 경로의 파일 이름 부분. |
| `__PATHFILENAME__` | `str` | 현재 include 파일의 전체 경로. |
| `__DIR__` | `str` | 현재 include 파일 경로의 디렉터리 부분. |
| `__INCLUDE__` | `str` | 현재 include 경로(여러 경로는 세미콜론으로 구분). |
| `__WIN32__` | `void` | 항상 정의됨. #ifdef로 ISPP 환경을 감지하는 데 사용할 수 있습니다. |
| `ISPP_INVOKED` | `void` | ISPP가 활성화되면 항상 정의됨. |
| `ISCC_INVOKED` | `void` | 콘솔 모드 컴파일러(ISCC.exe)로 컴파일할 때 정의됨. |
| `PREPROCVER` | `int` | Inno Setup 전처리기의 32비트 패킹된 버전 번호. |
| `Ver` | `int` | PREPROCVER의 별칭. |
| `WINDOWS` | `void` | 항상 정의됨. |
| `UNICODE` | `void` | 항상 정의됨(ISPP는 유니코드 전용). |
| `CompilerPath` | `str` | Inno Setup 컴파일러(ISCC.exe)가 위치한 디렉터리. |
| `SourcePath` | `str` | 루트 스크립트 파일이 들어 있는 디렉터리. |
| `SysPath` | `str` | 컴파일러 유형에 맞는 시스템 디렉터리. |
| `NewLine` | `str` | 줄 바꿈 문자 시퀀스. |
| `Tab` | `str` | 탭 문자. |

이들은 `{#…}` 완성에 표시되고 검증에서 허용됩니다. 경로 관련 변수(`{#SourcePath}`, `{#__DIR__}`, `{#CompilerPath}`, `{#SysPath}`)는 플러그인이 `[Languages]`의 `MessagesFile` 경로를 해석할 때도 확장됩니다. 나머지 동적 변수는 거짓 오류를 생성하지 않고 해석되지 않은 상태로 남습니다.

!!! note "값이 없는 기호"
    `__WIN32__`, `ISPP_INVOKED`, `ISCC_INVOKED`, `WINDOWS`, `UNICODE`는 **값이 없습니다**: 이들은 조건부 컴파일(`#ifdef` / `#if defined(...)`)을 위해서만 *정의*되므로 `{#…}`로 출력할 **수 없습니다**. `{#…}` 완성에서 제외되고 인라인 출력으로 허용되지 않습니다.
