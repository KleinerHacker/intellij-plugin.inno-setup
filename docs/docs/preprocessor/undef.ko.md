# `#undef`

`#undef`는 이전에 [`#define`](define.md)으로 선언한 매크로를 제거합니다. 그 이후로 해당 이름은 더 이상
정의되지 않습니다. `defined(Name)`은 거짓이 되고 이후 그 이름의 사용은 정의되지 않은 것으로 처리됩니다.

---

## 구문

```ini
#undef [Scope] Name
```

이름 앞에 선택적 범위 키워드(`public`, `protected`, `private`)를 붙일 수 있습니다([`#define`](define.md)과
동일).

---

## 설명

`#undef`는 일반적으로 값을 다시 정의하거나 기능 플래그를 지우기 위해 조건과 함께 사용됩니다:

```ini
#define EnableLogging
; … 나중에 …
#undef EnableLogging      ; 이 이후로 기능 플래그가 사라짐

#ifdef EnableLogging
  ; 더 이상 출력되지 않음
#endif
```

정의되지 않은 이름을 `#undef`해도 아무런 효과가 없습니다. `#undef`는 이름이 현재 정의되어 있는지
테스트하는 [조건 지시문](conditionals.md)과 함께 사용할 때 가장 유용합니다.

---

## 편집기 지원

- 지시문 키워드(및 선택적 범위 키워드)는 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해
  검증됩니다.
- `#undef Name`의 이름은 해당 `#define`으로 **확인됩니다** — 정의로 이동(**Ctrl+B** / **Cmd+B**),
  사용처 찾기(**Alt+F7**), 이름 바꾸기가 `#define`, `#undef`, 모든 `{#Name}` 사용을 동기화합니다.
- `#undef ` 뒤의 완성은 범위 키워드와 앞서 정의된 매크로 이름을 제시합니다.
- 일치하는 `#define`이 **없는** `#undef`는 아무 일도 하지 않습니다. 그 이름은 회색으로 표시되며 지시문을
  제거하는 빠른 수정이 제공됩니다.

---

공식 [`#define` / `#undef` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_define.htm).
