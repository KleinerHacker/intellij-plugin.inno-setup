# [Languages]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=languagessection){
.md-button .md-button--primary }

`[Languages]` 섹션은 설치 프로그램에서 사용 가능한 모든 언어를 선언합니다. 각 항목은 마법사 UI의 번역된 문자열을 제공하는 ISL 메시지 파일을 가리킵니다. 목록의 첫 번째 항목이 기본 언어가 됩니다. 여기에 정의된 언어는 다른 섹션의 `Languages` 공통 매개변수를 통해 참조하여 항목을 특정 로케일로 제한할 수 있습니다.

---

## Name

`string` · **필수**

내부 언어 식별자(예: `english`, `german`). 다른 섹션의 `Languages` 매개변수에서 참조됩니다.

---

## MessagesFile

`string` · **필수**

ISL 메시지 파일 경로. 내장 영어 메시지에는 `compiler:Default.isl`, 번들된 번역 중 하나에는 `compiler:Languages\German.isl`을 사용합니다.
