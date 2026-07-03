# [Dirs]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=dirssection){ .md-button
.md-button--primary }

`[Dirs]` 섹션은 설치 중에 대상 컴퓨터에 추가 디렉토리를 만듭니다. 대부분의 경우 이 섹션은 전혀 필요하지 않습니다——`[Files]`에 나열된 파일에 필요한 디렉토리는 자동으로 생성됩니다. 빈 디렉토리
구조를 만들거나, 특정 NTFS 속성을 설정하거나, 디렉토리에 ACL 권한을 구성할 때 `[Dirs]`를 사용하세요.

---

## Name

`string` · **필수**

만들 디렉토리의 전체 경로(예: `{app}\data`). 모든 Inno Setup 디렉토리 상수를 지원합니다.

---

## Attribs

`string` · **여러 값**

디렉토리에 설정할 파일 시스템 속성: `readonly`, `hidden`, `system`, `notcontentindexed`.

---

## Permissions

`string` · **여러 값**

디렉토리에 부여할 ACL 권한(예: `users-modify`, `everyone-readexec`). `{sys}` 또는 `{commonpf}` 같은 최상위 시스템 디렉토리에 ACL을 설정하지 마세요.

---

## Flags

`string` · **여러 값**

동작 플래그: `deleteafterinstall`, `setntfscompression`, `uninsalwaysuninstall`, `uninsneveruninstall`,
`unsetntfscompression`.

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

`[Code]`에서 `Boolean`을 반환하는 Pascal 함수 이름. 함수가 `True`를 반환하는 경우에만 처리됩니다.

---

## BeforeInstall

`string`

이 디렉토리가 생성되기 직전에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## AfterInstall

`string`

이 디렉토리가 생성된 직후에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## MinVersion

`string`

이 항목이 적용되는 최소 Windows 버전. `0`을 사용하면 적용되지 않습니다.

---

## OnlyBelowVersion

`string`

이 항목이 적용되는 최대 Windows 버전(제외). `0`을 사용하면 상한 없음.
