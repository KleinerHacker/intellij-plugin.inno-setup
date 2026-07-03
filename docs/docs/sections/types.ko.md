# [Types]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=typessection){ .md-button
.md-button--primary }

`[Types]` 섹션은 마법사의 컴포넌트 선택 페이지에 표시되는 명명된 설치 프로필을 정의합니다——예를 들어 *전체*, *컴팩트*, *사용자 지정*. `[Components]`의 각 컴포넌트는 하나 이상의 타입을
참조하여 기본적으로 어떤 프로필이 그것을 포함하는지 선언합니다. `iscustom` 플래그를 사용하여 사용자 지정 가능한 타입으로 표시할 수 있는 타입은 하나뿐입니다.

---

## Name

`string` · **필수**

이 설치 타입의 내부 식별자. `[Components]`의 `Types` 매개변수에서 참조됩니다.

---

## Description

`string` · **필수**

마법사에서 이 설치 타입에 표시되는 사람이 읽을 수 있는 레이블.

---

## Flags

`string` · **여러 값**

동작 플래그. `iscustom`은 이 타입을 사용자 지정 가능한 타입으로 표시합니다——스크립트당 하나의 타입만 이 플래그를 가질 수 있습니다.
