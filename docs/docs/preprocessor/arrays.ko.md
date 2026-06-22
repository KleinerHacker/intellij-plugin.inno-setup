# `#dim` / `#redim`

`#dim`은 **배열** 매크로를 선언하고, `#redim`은 기존 배열의 내용을 유지하면서 크기를 변경합니다. 배열을
사용하면 [`#for`](for.md)로 반복할 수 있는 인덱싱된 값 목록을 저장할 수 있습니다.

---

## 구문

```ini
#dim Name[Size]
#redim Name[NewSize]
```

- `#dim`은 지정된 요소 수로 배열 `Name`을 만듭니다.
- `#redim`은 이전에 선언된 배열의 크기를 조정합니다. 새 범위 내의 기존 요소는 보존됩니다.

---

## 설명

요소는 `Name[Index]`로 주소를 지정하고 [`#define`](define.md)으로 할당합니다. 일반적인 패턴은 배열을
채운 다음 순회하는 것입니다:

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < 3; i++} \
  #pragma message Servers[i]
```

요소 수를 나중에야 알 수 있을 때(예: 항목을 센 후) `#redim`을 사용합니다:

```ini
#redim Servers[5]   ; 처음 세 값을 유지하면서 배열 확장
```

---

## 편집기 지원

두 지시문 키워드 모두 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해 검증됩니다.

---

공식 [`#dim` / `#redim` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm).
