# `#if` / `#elif` / `#else` / `#endif` 및 `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`

조건 지시문은 컴파일 시 스크립트의 일부를 포함하거나 제외합니다. 여는 지시문과 그에 대응하는 `#endif`
사이의 모든 것은 조건이 성립할 때만 출력됩니다.

---

## 구문

```ini
#if Expression
#elif Expression
#else
#endif

#ifdef Name
#ifndef Name
#ifexist "filename"
#ifnexist "filename"
```

---

## 설명

- **`#if` / `#elif` / `#else` / `#endif`**는 정수 식(0이 아니면 참)으로 구동되는 if/else-if/else 체인을
  구성합니다. `#elif`와 `#else`는 선택 사항이며, 모든 블록은 `#endif`로 닫아야 합니다.
- **`#ifdef` / `#ifndef`**는 매크로가 정의되어 있는지(아닌지) 테스트합니다. `#if defined(Name)`과
  `#if !defined(Name)`의 약식입니다.
- **`#ifexist` / `#ifnexist`**는 파일이 디스크에 존재하는지(아닌지) 테스트합니다.

```ini
#define Beta

#ifdef Beta
  #define AppSuffix " (Beta)"
#else
  #define AppSuffix ""
#endif

#if VER >= 0x06000000
  ; 최신 Inno Setup 버전 전용
#endif

#ifexist "extra\\readme.txt"
  #include "extra\\readme.txt"
#endif
```

---

## 편집기 지원

- **강조 표시 및 완성** — 모든 조건 키워드는 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해
  검증됩니다.
- **조건 식** — `#if` / `#elif` 조건은 [`#define`](define.md) 값과 동일한 완전한 ISPP 식입니다. 연산자가
  강조 표시되고, 구문 및 형식 오류가 해당 토큰에 보고되며, 식 완성 후보(다른 `#define`, 미리 정의된 변수,
  내장 함수)가 조건 안에서도 제시됩니다.
- **참조** — 조건의 식별자는 `#define` 선언으로 확인되므로 정의로 이동(**Ctrl+B** / **Cmd+B**), 사용처
  찾기(**Alt+F7**), 이름 바꾸기가 작동합니다. 알 수 없는 이름은 *확인되지 않은 참조* 오류로
  표시됩니다(`#define`과 동일). `defined(Name)`은 예외로, 그 인수는 정의되지 않아도 됩니다.
- **불리언 리터럴** — ISPP에는 불리언이 없으므로 조건에서 직접 사용된 `true` / `false` / `yes` / `no`는
  **노란색**으로 표시되며 경고가 붙습니다(해당 단어는 정의되지 않은 식별자 `0`으로 처리됨).
- **구조 검증** — 모든 여는 지시문(`#if` / `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`)은 파일 끝
  이전에 `#endif`로 닫혀야 합니다. 닫히지 않은 여는 지시문, 열린 블록이 없는 `#elif` / `#else` / `#endif`,
  `#else` 뒤의 `#elif`는 모두 오류로 표시됩니다. 조건이 없는 `#if` / `#elif`도 오류입니다.
- **접기** — 완전한 `#if … #endif` 블록은 단일 섹션 내부에 완전히 있거나 어떤 섹션에도 속하지 않을 때
  접을 수 있습니다(섹션 헤더를 가로지르는 블록은 접히지 않음).

---

공식 [`#if` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm).
