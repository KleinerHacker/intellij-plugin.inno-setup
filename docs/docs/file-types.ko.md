# 파일 형식

이 플러그인은 IntelliJ 플랫폼 내에 두 가지 전용 파일 형식을 등록합니다. 각 파일 형식에는 고유한 아이콘, 언어 인프라, 편집기 기능 세트가 있습니다.

![Inno Setup 파일 형식](assets/images/filetypes.png)

---

## 개요

| 파일 형식                   | 확장자    | 용도                                                         |
|-----------------------------|-----------|--------------------------------------------------------------|
| **Inno Setup 스크립트**     | `.iss`    | 기본 설치 프로그램 정의 — 섹션, 파일, 레지스트리, 코드      |
| **Inno Setup 언어 파일**    | `.isl`    | 단일 언어에 대한 번역된 메시지 및 로케일 옵션               |

두 파일 형식 모두 확장자에 의해 자동으로 인식됩니다. 수동 연결이 필요하지 않습니다.

---

## Inno Setup 스크립트(`.iss`)

`.iss` 파일은 Inno Setup의 기본 형식입니다. 패키지할 파일, 작성할 레지스트리 키, 제공할 언어, 그리고 선택적으로 `[Code]` 섹션의 Pascal 스크립트 런타임 로직 등 전체 설치 프로그램을 설명합니다. ISPP 전처리기 지시문(`#define`, `#include`…)은 파일 상단에 나타날 수 있습니다.

지원되는 섹션 및 편집 기능의 전체 목록은 [스크립트 파일](script-files.md)을 참조하세요.

---

## Inno Setup 언어 파일(`.isl`)

`.isl` 파일은 단일 로케일에 대한 번역된 문자열을 제공합니다. `[Languages]`의 `MessagesFile:` 매개변수를 통해 `.iss` 스크립트에서 참조되며 내장된 Inno Setup 메시지의 임의 하위 집합을 재정의할 수 있습니다. 프로젝트별 사용자 지정 메시지는 `[CustomMessages]`에도 배치할 수 있습니다.

지원되는 섹션 및 편집 기능의 전체 목록은 [언어 파일](language-files.md)을 참조하세요.

---

## 파일 형식 간의 관계

`.iss` 스크립트는 `[Languages]` 섹션을 통해 하나 이상의 `.isl` 파일을 참조할 수 있습니다:

```ini
[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german";  MessagesFile: "compiler:Languages\German.isl"
```

플러그인은 파일 간에 이러한 참조를 해결합니다: `[Languages]`에서 선언된 언어 이름은 `[Messages]` 및 `[CustomMessages]` 내의 언어 접두사 참조(예: `german.WelcomeLabel1`)의 대상이며, 스크립트 전체의 값 내 `{cm:…}` 상수의 대상이기도 합니다.
