# [ISSigKeys]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=issigkeyssection){ .md-button
.md-button--primary }

`[ISSigKeys]` 섹션은 `.issig` 파일 서명을 검증하는 데 사용되는 공개 키를 정의합니다. 이러한 키는 `[Files]`의 `ISSigAllowedKeys` 매개변수와 `issigverify`
플래그에서 참조됩니다.

*이 섹션은 Inno Setup 6.5부터 사용할 수 있습니다.*

---

## Name

`string` · **필수**

이 키 항목의 식별자. `[Files]` 항목은 `ISSigAllowedKeys`를 통해 이를 참조합니다.

---

## Group

`string`

`ISSigAllowedKeys`에서 여러 키가 식별자를 공유할 수 있도록 하는 논리적 그룹 이름.

---

## KeyFile

`string`

공개 키 데이터를 포함하는 키 파일 경로.

---

## PublicX

`string`

공개 EC 키 X 좌표의 16진수 인코딩.

---

## PublicY

`string`

공개 EC 키 Y 좌표의 16진수 인코딩.

---

## KeyID

`string`

키 조회를 위해 설치 프로그램에 포함되는 선택적 컴파일 시간 키 식별자.

---

## RuntimeID

`string`

키 조회를 위해 설치 시 설치 프로그램이 사용하는 선택적 런타임 키 식별자.
