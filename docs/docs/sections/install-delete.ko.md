# [InstallDelete]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=installdeletesection){
.md-button .md-button--primary }

`[InstallDelete]` 섹션은 설치 *시작* 시 새 파일이 복사되기 전에 Inno Setup이 삭제하는 파일과 디렉토리를 나열합니다. 이것은 새 설치 프로그램이 더 이상 추적하지 않는 애플리케이션 이전
버전이 남긴 오래된 파일이나 이전 디렉토리 구조를 정리하는 데 유용합니다.

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
