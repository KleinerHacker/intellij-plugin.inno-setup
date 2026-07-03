# [INI]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=inissection){ .md-button
.md-button--primary }

!!! warning "플러그인 지원"
`[INI]`는 현재 플러그인 사양에서 누락된 것으로 표시됩니다. 이 섹션은 문서 커버리지를 위해 여기에 나열되어 있지만 사양이 구현될 때까지 완성 및 유효성 검사 지원이 불완전할 수 있습니다.

`[INI]` 섹션은 설치 중에 사용자 시스템의 `.ini` 파일 항목을 만들거나 업데이트합니다. 설정을 레지스트리나 애플리케이션 데이터 파일 대신 INI 파일에 저장하는 레거시 애플리케이션에 유용합니다.

---

## Filename

`string` · **필수**

수정할 `.ini` 파일 경로(예: `{app}\MyApp.ini` 또는 `{win}\MyApp.ini`).

---

## Section

`string` · **필수**

키를 포함하는 INI 섹션 이름.

---

## Key

`string`

만들거나 업데이트하거나 삭제할 INI 키 이름.

---

## String

`string`

키에 쓸 값.

---

## Flags

`string` · **여러 값**

동작 플래그: `createkeyifdoesntexist`, `uninsdeleteentry`, `uninsdeletesection`, `uninsdeletesectionifempty`.

---

## Components

`→ Components` · **여러 값**

나열된 컴포넌트 중 하나 이상이 선택된 경우에만 처리됩니다.

---

## Tasks

`→ Tasks` · **여러 값**

나열된 태스크 중 하나 이상이 체크된 경우에만 처리됩니다.

---

## Languages

`→ Languages` · **여러 값**

이 항목을 지정된 언어로 제한합니다.

---

## Check

`string`

`[Code]`에서 `Boolean`을 반환하는 Pascal 함수 이름. 함수가 `True`를 반환하는 경우에만 처리됩니다.

---

## MinVersion

`string`

이 항목이 적용되는 최소 Windows 버전. `0`을 사용하면 적용되지 않습니다.

---

## OnlyBelowVersion

`string`

이 항목이 적용되는 최대 Windows 버전(제외). `0`을 사용하면 상한 없음.
