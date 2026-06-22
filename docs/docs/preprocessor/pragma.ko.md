# `#pragma`

`#pragma`는 전처리기 자체를 제어합니다. 출력을 생성하는 대신 **하위 명령**을 받아 전처리기가 스크립트를
읽고 구문 분석하고 보고하는 방식을 조정합니다. 플러그인은 내장 사양에서 모든 하위 명령을 알고 있으며 그
뒤에 오는 인수를 검증합니다.

---

## 구문

```ini
#pragma <sub-command> [arguments]
```

`#pragma` 뒤의 첫 단어가 하위 명령이고 그 뒤의 모든 것은 인수입니다. 알 수 없는 하위 명령은 오류로
보고되며, 누락되거나 형식이 잘못된 인수도 마찬가지입니다.

---

## 하위 명령

| 하위 명령 | 인수 | 목적 |
|-----------|------|------|
| `option` | 옵션 플래그 | 전처리기의 일반 읽기/출력 옵션 |
| `parseroption` | 옵션 플래그 | 식 구문 분석을 제어하는 옵션 |
| `message` | 문자열 | 컴파일러 창에 정보 메시지 출력 |
| `warning` | 문자열 | 컴파일러 창에 경고 출력 |
| `error` | 문자열 | 대화 상자에 오류 메시지 표시 |
| `verboselevel` | 정수(0–10) | 메시지의 상세도 임계값 설정 |
| `inlinestart` | 문자열 | 인라인 지시문의 여는 구분 기호 설정(기본값 `{#`) |
| `inlineend` | 문자열 | 인라인 지시문의 닫는 구분 기호 설정(기본값 `}`) |
| `include` | 문자열 | 포함 파일의 세미콜론으로 구분된 검색 경로 설정 |
| `spansymbol` | 문자열 | 줄 연속 문자 설정(첫 글자만) |

---

## 옵션 플래그: `option`과 `parseroption`

`option`과 `parseroption`은 `-<letter>(+|-)` 형식의 플래그를 공백으로 구분하여 하나 이상 받습니다.
`+`는 옵션을 켜고 `-`는 끕니다. 플러그인은 알 수 없는 문자나 잘못된 형식의 플래그(대시 또는 부호 누락)를
오류로 보고합니다.

### `option` 플래그

| 플래그 | 기본값 | 의미 |
|------|------|------|
| `c` | 켜짐 | 컴파일러로 출력 |
| `e` | 켜짐 | 빈 줄 출력 |
| `v` | 꺼짐 | 자세한 모드 |

### `parseroption` 플래그

| 플래그 | 기본값 | 의미 |
|------|------|------|
| `b` | 켜짐 | 불리언 단락 평가 |
| `m` | 꺼짐 | 곱셈 단락 평가 |
| `p` | 켜짐 | Pascal 스타일 문자열 리터럴 |
| `u` | 꺼짐 | 선언되지 않은 식별자 허용 |

```ini
#pragma option -v+            ; 자세한 출력 활성화
#pragma parseroption -b- -u+  ; 불리언 단락 비활성화, 선언되지 않은 식별자 허용
```

---

## 식 하위 명령

`message`, `warning`, `error`, `include`, `inlinestart`, `inlineend`, `spansymbol`은 **문자열 식**을,
`verboselevel`은 **정수 식**을 받습니다. 플러그인은 `#define`과 동일한 엔진으로 인수를 구문 분석하고
형식을 검사하므로 잘못된 형식(문자열이 필요한 곳의 숫자 등)은 표시되며 `verboselevel`은 0–10 범위여야
합니다.

```ini
#define BuildId 42
#pragma message "Building configuration #" + Str(BuildId)
#pragma verboselevel 9
#pragma inlinestart "$("
#pragma inlineend ")"
```

이러한 식 내부의 식별자는 `#define` 값 내부에서와 마찬가지로 `#define`을 참조합니다. 해석되고 정의로
이동, 사용 찾기, 이름 바꾸기를 지원하며 알 수 없는 이름은 해석되지 않은 참조로 표시됩니다.

---

## 편집기 지원

- 하위 명령 이름, 옵션 플래그 문자와 형식, 인수 형식의 **검증**
- `#pragma ` 뒤 하위 명령의 **완성** 및 `#pragma option `/`#pragma parseroption ` 뒤 옵션 플래그의 완성
- 식 인수 내에서 사용된 `#define`의 **참조 해석, 사용 찾기, 이름 바꾸기**

---

전체 참조는 공식 [`#pragma` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_pragma.htm)를
참조하세요.
