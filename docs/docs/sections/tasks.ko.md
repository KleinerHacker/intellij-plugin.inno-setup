# [Tasks]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=taskssection){
.md-button .md-button--primary }

`[Tasks]` 섹션은 사용자가 *추가 작업 선택* 마법사 페이지에서 활성화하거나 비활성화할 수 있는 선택적 작업을 정의합니다——바탕화면 바로 가기 만들기나 컨텍스트 메뉴 항목 추가 등. 태스크는 체크박스로 표시되거나 그룹 내에서 `exclusive`로 표시된 경우 라디오 버튼으로 표시됩니다. 다른 섹션의 항목은 `Tasks` 매개변수를 통해 태스크에 연결됩니다.

---

## Name

`string` · **필수**

이 태스크의 내부 식별자. 하위 태스크에는 백슬래시 표기법 사용(예: `desktopicon\user`).

---

## Description

`string` · **필수**

마법사에서 체크박스 또는 라디오 버튼 옆에 표시되는 레이블.

---

## GroupDescription

`string`

관련 태스크 그룹 위에 표시되는 선택적 제목.

---

## Components

`→ Components` · **여러 값**

나열된 컴포넌트 중 하나 이상이 선택된 경우에만 이 태스크가 표시됩니다.

---

## Flags

`string` · **여러 값**

동작 플래그: `checkablealone`, `checkedonce`, `dontinheritcheck`, `exclusive`, `restart`, `unchecked`.

---

## Check

`string`

`[Code]`에서 `Boolean`을 반환하는 Pascal 함수 이름. 함수가 `True`를 반환하는 경우에만 이 항목이 처리됩니다.

---

## Languages

`→ Languages` · **여러 값**

이 항목을 지정된 언어로 제한합니다.
