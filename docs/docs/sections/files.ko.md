# [Files]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=filessection){ .md-button
.md-button--primary }

`[Files]` 섹션은 설치 프로그램이 대상 컴퓨터에 복사해야 하는 모든 파일을 선언하는 곳입니다. 와일드카드, 재귀 디렉토리 트리, 글꼴 설치, .NET GAC 등록, 파일 무결성 해싱, 심지어 설치 시
인터넷에서 파일 다운로드도 지원합니다. 나열된 파일에 필요한 디렉토리는 자동으로 생성됩니다——`[Dirs]` 항목이 필요하지 않습니다.

---

## Source

`string` · **필수**

소스 파일 경로 또는 와일드카드 패턴(예: `MyProg.exe` 또는 `Plugins\*`). `SourceDir` 기준 상대 경로.

---

## DestDir

`string` · **필수**

대상 컴퓨터의 대상 디렉토리(예: `{app}` 또는 `{sys}`). 모든 Inno Setup 상수를 지원합니다.

---

## DestName

`string`

대상 컴퓨터에서 파일 이름을 변경합니다. 생략하면 원래 파일 이름이 유지됩니다.

---

## Excludes

`string`

와일드카드 사용 시 제외할 파일 이름 패턴의 쉼표로 구분된 목록(예: `*.pdb,*.log`).

---

## ExternalSize

`integer`

외부 파일의 바이트 단위 크기(`external` 플래그와 함께 사용). 마법사 페이지에서 정확한 디스크 공간 계산에 사용됩니다.

---

## Attribs

`string` · **여러 값**

파일 설치 후 설정할 파일 시스템 속성: `readonly`, `hidden`, `system`, `notcontentindexed`.

---

## Permissions

`string` · **여러 값**

설치된 파일에 설정할 ACL 권한(예: `users-modify`, `everyone-readexec`).

---

## FontInstall

`string`

글꼴 파일 설치 시 사용하는 레지스트리 글꼴 이름(예: `My Font (TrueType)`). Windows에서 글꼴 등록을 트리거합니다.

---

## StrongAssemblyName

`string`

전역 어셈블리 캐시(GAC) 등록을 위한 .NET 강력한 이름 어셈블리 이름.

---

## Hash

`string` · **6.5 이상**

소스 파일의 예상 SHA-256 해시. Inno Setup이 컴파일 시 해시를 검증하여 우발적인 파일 손상을 감지합니다.

---

## ISSigAllowedKeys

`string` · **여러 값** · **6.5 이상**

파일의 `.issig` 서명을 검증하는 데 사용되는 `[ISSigKeys]`의 키 식별자 쉼표로 구분된 목록.

---

## ExtractArchivePassword

`string` · **6.5 이상**

암호화된 아카이브의 비밀번호(`extractarchive` 플래그와 함께 사용). 설치 프로그램 내부에 암호화되지 않은 상태로 저장됩니다.

---

## DownloadISSigSource

`string` · **6.5 이상**

설치 시 다운로드된 파일의 `.issig` 서명 파일 URL.

---

## DownloadUserName

`string` · **6.5 이상**

인증된 파일 다운로드를 위한 HTTP 기본 인증 사용자 이름(`download` 플래그 필요).

---

## DownloadPassword

`string` · **6.5 이상**

인증된 파일 다운로드를 위한 HTTP 기본 인증 비밀번호(`download` 플래그 필요).

---

## Flags

`string` · **여러 값**

동작 플래그: `32bit`, `64bit`, `comparetimestamp`, `confirmoverwrite`, `deleteafterinstall`, `dontcopy`, `download`,
`external`, `extractarchive`, `ignoreversion`, `isreadme`, `nocompression`, `onlyifdoesntexist`, `recursesubdirs`,
`createallsubdirs`, `regserver`, `regtypelib`, `restartreplace`, `sharedfile`.

---

## Components

`→ Components` · **여러 값**

나열된 컴포넌트 중 하나 이상이 선택된 경우에만 이 항목이 처리됩니다.

---

## Tasks

`→ Tasks` · **여러 값**

나열된 태스크 중 하나 이상이 체크된 경우에만 이 항목이 처리됩니다.

---

## Languages

`→ Languages` · **여러 값**

이 항목을 지정된 언어로 제한합니다.

---

## Check

`string`

`[Code]`에서 `Boolean`을 반환하는 Pascal 함수 이름. 함수가 `True`를 반환하는 경우에만 이 항목이 처리됩니다.

---

## BeforeInstall

`string`

이 파일이 설치되기 직전에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## AfterInstall

`string`

이 파일이 설치된 직후에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## MinVersion

`string`

이 항목이 적용되는 최소 Windows 버전. `0`을 사용하면 적용되지 않습니다.

---

## OnlyBelowVersion

`string`

이 항목이 적용되는 최대 Windows 버전(제외). `0`을 사용하면 상한 없음.
