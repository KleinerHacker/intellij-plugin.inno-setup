# [Messages]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=messagessection){ .md-button
.md-button--primary }

`[Messages]` 섹션은 `Default.isl` 또는 선택된 언어 파일의 내장 설치 프로그램 UI 문자열을 재정의합니다. 각 항목은 지시문 구문을 사용합니다:

```ini
MessageID=Text
german.MessageID=Text
```

선택적 언어 접두사는 `[Languages]`에서 선언된 단일 언어를 대상으로 합니다.

---

## 알려진 메시지 ID

플러그인은 표준 `Default.isl` 메시지 식별자를 알려진 키로 포함합니다. 완성 기능은 메시지 ID를 제안하며, 점 앞에서 사용될 때 사용 가능한 언어 접두사를 제안합니다.

---

## 언어 접두사

언어 접두사는 `[Languages]` 항목으로 해결됩니다. 대상 언어가 해결될 수 있으면 플러그인은 이러한 접두사에 대한 완성, 탐색, 사용 찾기, 이름 바꾸기, 언어 국기 인레이를 지원합니다.

---

## 값

`string`

Setup 또는 Uninstall이 표시하는 메시지 텍스트. 런타임 대체를 기대하는 메시지를 재정의할 때 `%1` 및 `%2` 같은 자리 표시자를 유지하세요.
