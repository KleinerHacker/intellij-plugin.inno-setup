# `#error`

`#error`는 컴파일을 즉시 중단하고 지정된 메시지를 보고합니다. 필수 조건이 충족되지 않을 때 — 예를 들어
누락된 매크로나 지원되지 않는 구성 — 빌드를 실패시키는 데 사용합니다.

---

## 구문

```ini
#error Message
```

메시지는 줄의 나머지 부분입니다. 사용자에게 표시되고 컴파일이 중단됩니다.

---

## 설명

`#error`는 일반적으로 잘못된 경우에만 발생하도록 [조건](conditionals.md)으로 보호됩니다:

```ini
#ifndef AppVersion
  #error AppVersion must be defined before including this file
#endif
```

문자열 **식**을 받는 `#pragma error`와 달리 `#error`는 줄의 나머지를 일반 메시지로 취급합니다.

---

## 편집기 지원

지시문 키워드는 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해 검증됩니다.

---

공식 [`#error` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_error.htm).
