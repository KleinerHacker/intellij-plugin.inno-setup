# [UninstallDelete]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=uninstalldeletesection){ .md-button .md-button--primary }

`[UninstallDelete]` 섹션은 제거 중에 Inno Setup이 원래 추적한 파일 외에 추가로 삭제하는 파일과 디렉토리를 나열합니다. 런타임에 생성되어 `[Files]`에 나열된 적 없는 생성 파일, 캐시, 로그 파일 또는 사용자 데이터 디렉토리를 정리하는 데 사용합니다.

---

## Type

`string` · **필수**

삭제할 대상: `files`(일치하는 파일만), `filesandordirs`(파일 및 모든 하위 디렉토리), `dirifempty`(파일이 없는 경우에만 디렉토리).

---

## Name

`string` · **필수**

삭제할 파일 또는 디렉토리의 경로 또는 와일드카드 패턴(예: `{app}\*.log` 또는 `{app}\cache`).

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
