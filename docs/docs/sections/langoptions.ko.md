# [LangOptions]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=langoptionssection){ .md-button .md-button--primary }

`[LangOptions]` 섹션은 언어별 표시 설정을 정의합니다. `.isl` 언어 파일에서는 필수이며 스크립트에서 선언된 언어의 옵션을 재정의하는 데도 사용할 수 있습니다. 스크립트에서 지시문 이름에 언어 이름을 접두사로 붙일 수 있습니다(예: `german.DialogFontName=Segoe UI`).

---

## LanguageName

`string` · **.isl에서 필수**

언어 선택 대화 상자에 표시되는 언어의 기본 이름(예: `Deutsch`).

---

## LanguageID

`integer` · **.isl에서 필수**

자동 언어 감지에 사용되는 Windows 언어 식별자. 일반적으로 Pascal 스타일 16진수로 작성됩니다(예: 영어(미국)는 `$0409`, 독일어(독일)는 `$0407`). 완성 기능은 번들된 Windows LCID 목록을 사용합니다.

---

## LanguageCodePage

`integer`

메시지 파일에서 비유니코드 텍스트를 변환하는 데 사용되는 코드 페이지. 파일에 유니코드 또는 ASCII 텍스트만 포함된 경우 `0`을 사용합니다.

---

## DialogFontName

`string`

대부분의 마법사 텍스트에 사용되는 글꼴. 비워두면 Segoe UI가 기본값이 됩니다.

---

## DialogFontSize

`integer`

대화 상자 글꼴의 포인트 크기. 기본값: `9`.

---

## DialogFontBaseScaleWidth

`integer`

대화 상자 글꼴에 대해 대화 상자 컨트롤을 크기 조정하는 데 사용되는 기본 너비(픽셀). 기본값: `7`.

---

## DialogFontBaseScaleHeight

`integer`

대화 상자 글꼴에 대해 대화 상자 컨트롤을 크기 조정하는 데 사용되는 기본 높이(픽셀). 기본값: `15`.

---

## WelcomeFontName

`string`

환영 페이지 및 설치 완료 페이지의 큰 제목에 사용되는 글꼴.

---

## WelcomeFontSize

`integer`

환영 글꼴의 포인트 크기. 기본값: `14`.

---

## RightToLeft

`integer`

오른쪽에서 왼쪽으로 쓰는 언어에는 `1`, 왼쪽에서 오른쪽으로 쓰는 언어에는 `0`으로 설정합니다.

---

## 제거된 지시문

`TitleFontName`, `TitleFontSize`, `CopyrightFontName`, `CopyrightFontSize`는 Inno Setup 6.4에서 제거되었습니다. 플러그인은 이전 스크립트와의 호환성을 위해 제거 버전과 함께 표시하여 보존합니다.
