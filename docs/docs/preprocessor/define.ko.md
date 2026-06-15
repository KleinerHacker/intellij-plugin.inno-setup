# `#define`

`#define`은 전처리기 매크로 — 컴파일 시점에 스크립트로 치환되는 명명된 값 또는 표현식 — 를 선언합니다. 가장 많이 사용되는 ISPP 지시문이며, 플러그인이 완전한 의미 체계(참조 해석, 이름 바꾸기, 사용 찾기)로 지원하는 지시문입니다.

---

## 구문

```ini
#define Name [Value]
#define Name(Param1, Param2) Expression
```

- `#define Name Value`는 상수 매크로를 정의합니다(값을 생략하면 *void* 매크로가 됩니다).
- `#define Name(params) Expression`은 함수형 매크로를 정의합니다. 플러그인은 표현식 본문이 없는 함수형 매크로를 오류로 표시합니다.
- `#undef Name`은 이전에 정의된 매크로를 제거합니다.

---

## 매크로 사용: `{#Name}`

일반 스크립트 줄 안에서 `{#Name}`(`{#emit Name}`의 축약형)은 매크로의 값을 출력합니다:

```ini
#define MyAppVersion "1.5.0"

[Setup]
AppVersion={#MyAppVersion}
OutputBaseFilename=setup-{#MyAppVersion}
```

- `{#Name}`은 해당 `#define` 선언으로 해석됩니다 — 정의로 이동(**Ctrl+B** / **Cmd+B**)과 사용 찾기(**Alt+F7**)가 작동하며, 이름 바꾸기는 선언과 모든 사용을 동기화합니다.
- `{` 바로 뒤와 `{#` 뒤 모두에서 완성으로 제공됩니다.
- 한 번도 사용되지 않은 `#define`은 표시되며 제거 빠른 수정이 제공됩니다.

---

## 표준 사전 정의 변수

직접 만든 `#define` 외에도 ISPP는 선언 없이 사용할 수 있는 **표준 사전 정의 변수** 집합을 제공합니다. **값을 가진** 변수는 사용자 정의와 마찬가지로 `{#…}`로 인라인 출력할 수 있습니다:

| 변수 | 의미 |
|------|------|
| `{#SourcePath}` | 루트 스크립트 파일의 디렉터리 |
| `{#CompilerPath}` | Inno Setup 컴파일러(`ISCC.exe`)의 디렉터리 |
| `{#SysPath}` | 시스템 디렉터리 |
| `{#__FILENAME__}`, `{#__PATHFILENAME__}`, `{#__DIR__}`, `{#__INCLUDE__}` | 현재 파일/경로 구성 요소 |
| `{#__LINE__}`, `{#__COUNTER__}` | 현재 줄 번호 / 자동 증가 카운터 |
| `{#Ver}`, `{#PREPROCVER}` | 전처리기 버전 |
| `{#NewLine}`, `{#Tab}` | 리터럴 제어 문자 |

이들은 `{#…}` 완성에 표시되고 검증에서 허용됩니다. 경로 관련 변수(`{#SourcePath}`, `{#__DIR__}`, `{#CompilerPath}`, `{#SysPath}`)는 플러그인이 `[Languages]`의 `MessagesFile` 경로를 해석할 때도 확장됩니다. 나머지 동적 변수는 거짓 오류를 생성하지 않고 해석되지 않은 상태로 남습니다.

!!! note "값이 없는 기호"
    `__WIN32__`, `ISPP_INVOKED`, `ISCC_INVOKED`, `WINDOWS`, `UNICODE`는 **값이 없습니다**: 이들은 조건부 컴파일(`#ifdef` / `#if defined(...)`)을 위해서만 *정의*되므로 `{#…}`로 출력할 **수 없습니다**. `{#…}` 완성에서 제외되고 인라인 출력으로 허용되지 않습니다.
