# [CustomMessages]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=custommessagessection){ .md-button .md-button--primary }

`[CustomMessages]` 섹션은 프로젝트별 현지화 가능한 문자열을 정의합니다. 이러한 문자열은 `{cm:MessageName}` 상수를 통해 다른 섹션과 Pascal 코드에서 참조할 수 있습니다.

```ini
[CustomMessages]
WelcomeText=Welcome to My App
german.WelcomeText=Willkommen bei My App
```

---

## 메시지 이름

`string`

이 섹션에는 사전 정의된 키가 없습니다. 메시지 이름은 스크립트 작성자가 선택하며 선택적으로 언어 이름을 접두사로 붙일 수 있습니다(예: `german.WelcomeText`).

---

## 참조

`{cm:MessageName}`은 일치하는 `[CustomMessages]` 항목으로 해결됩니다. 플러그인은 `{cm:` 이후의 완성, 사용 찾기, 이름 바꾸기 리팩터링, 해결되지 않은 참조 강조 표시를 지원합니다.

메시지 이름이 바뀌면 플러그인은 언어 변형과 `{cm:...}` 사용을 동기화하여 유지합니다.
