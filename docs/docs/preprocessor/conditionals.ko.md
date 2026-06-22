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

모든 조건 키워드는 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해 검증되고, 제어 식은 구문 분석되고
형식이 검사됩니다.

---

공식 [`#if` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm).
