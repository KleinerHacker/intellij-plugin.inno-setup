# [Components]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=componentssection){ .md-button .md-button--primary }

`[Components]` 섹션은 마법사의 컴포넌트 선택 페이지에 표시되는 선택 가능한 기능을 정의합니다. 컴포넌트는 백슬래시 표기법을 사용하여 부모/자식 계층으로 구성할 수 있습니다(예: `extra\plugins`). `[Files]`, `[Icons]` 등의 섹션 항목은 `Components` 매개변수를 통해 컴포넌트에 연결되어 선택된 컴포넌트에 속한 파일만 설치됩니다.

---

## Name

`string` · **필수**

이 컴포넌트의 내부 식별자. 계층 구조에는 백슬래시 표기법 사용(예: `main\help`).

---

## Description

`string` · **필수**

마법사의 컴포넌트 선택 목록에 표시되는 이 컴포넌트의 레이블.

---

## Types

`→ Types` · **여러 값**

기본적으로 이 컴포넌트를 포함하는 설치 타입 이름(`[Types]`에서)의 공백으로 구분된 목록.

---

## ExtraDiskSpaceRequired

`integer`

이 컴포넌트가 설치하는 파일 외에 필요한 추가 디스크 공간(바이트). 컴포넌트 선택 페이지에 표시됩니다.

---

## Flags

`string` · **여러 값**

동작 플래그: `fixed`, `checkablealone`, `exclusive`, `restart`, `dontinheritcheck`, `disablenouninstallwarning`.

---

## Check

`string`

`[Code]`에서 `Boolean`을 반환하는 Pascal 함수 이름. 함수가 `True`를 반환하는 경우에만 이 항목이 처리됩니다.

---

## Languages

`→ Languages` · **여러 값**

이 항목을 지정된 언어로 제한합니다.
