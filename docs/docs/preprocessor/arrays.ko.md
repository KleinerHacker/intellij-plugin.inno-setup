# `#dim` / `#redim`

`#dim` 은 **배열** 매크로를 선언하고, `#redim` 은 기존 배열의 내용을 유지하면서 크기를 변경합니다. 배열을
사용하면 [`#for`](for.md) 로 순회할 수 있는 인덱스 기반 값 목록을 저장할 수 있습니다.

---

## 구문

```ini
#dim [private | protected | public] Name[Size] [{ Init, Init, ... }]
#redim [private | protected | public] Name[NewSize]
```

- `Size` / `NewSize` 는 정수 식입니다(리터럴, 다른 `#define`, `DimOf(...)` 등).
- `#dim` 은 해당 요소 수로 배열 `Name` 을 만듭니다. 각 요소는 처음에 **void**(빈 값)입니다.
- 선택적 `{ ... }` 목록은 선행 요소를 순서대로 초기화합니다(`{1, 2, 3}` 은 인덱스 `0`~`2` 를 채움).
- `#redim` 은 이전에 선언된 배열의 크기를 변경합니다. 새 범위 내의 기존 요소는 보존되고 새로 추가된 요소는
  void 입니다.
- 선택적 스코프 키워드(`private` / `protected` / `public`)는 [`#define`](define.md) 과 동일하게 가시성을
  제어합니다.

---

## 설명

배열은 **0 기반**입니다. 크기가 `N` 인 배열은 유효한 인덱스 `0 … N-1` 을 가집니다. 요소는 `Name[Index]` 로
지정하고 [`#define`](define.md) 으로 할당합니다. 일반적인 패턴은 배열을 채운 다음 순회하는 것입니다:

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < DimOf(Servers); i++} \
  #pragma message Servers[i]
```

인라인 초기화를 사용하면 더 간결하게 작성할 수 있습니다:

```ini
#dim Servers[3] {"alpha", "beta", "gamma"}
```

요소 값은 다른 값과 마찬가지로 식에 참여합니다. 읽기, 결합, 다른 매크로 참조가 가능합니다:

```ini
#define Base 10
#dim Offsets[2] {Base, Base + 5}
#if Offsets[1] > Offsets[0]
  ; ...
#endif
```

요소 수가 나중에야 알려지는 경우(예: 항목을 센 후)에는 `#redim` 을 사용합니다:

```ini
#redim Servers[5]   ; 처음 세 값을 유지하면서 배열 확장
```

`DimOf(Name)` 은 배열의 현재 요소 수를 반환합니다.

---

## 에디터 지원

- `#dim` / `#redim` 키워드, 스코프 키워드, 배열 이름이 강조 표시되고 번들된 ISPP 사양에 따라 검증됩니다.
  `[` 와 `]` 는 괄호 쌍으로 매칭됩니다.
- 배열 이름은 자동 완성으로 제공됩니다: `#redim ` 뒤(기존 배열)와 식 안(선언된 배열, 끝에 `[]` 표시).
- `Name[Index]`, `#redim Name`, `#define Name[Index]`, `DimOf(Name)` 은 모두 원본 `#dim` 과 함께 이동
  (Ctrl/Cmd-클릭) 및 이름 변경됩니다.
- 요소 값은 `#dim`/`#define` 에 걸쳐 정적으로 평가될 수 있습니다(예: 문서 팝업).

다음 실수는 오류로 보고됩니다:

- 배열이 **아닌** 이름에 대한 인덱싱(`Foo` 가 일반 `#define` 일 때의 `Foo[0]`);
- 식에서 인덱스 **없이** 배열 이름 사용(`Servers[i]` 대신 `Servers`);
- **정수가 아닌** 배열 인덱스 또는 크기;
- `#dim` 으로 선언되지 않은 배열의 `#redim`;
- 선언된 크기와 일치하지 않는 요소 수의 인라인 초기화;
- 정적으로 **범위를 벗어난** 인덱스(크기 3 배열에 대한 `#define Servers[9]` 또는 `Servers[9]`).

동적 인덱스(예: 루프 변수)는 거짓 양성을 피하기 위해 의도적으로 **표시되지 않습니다**.

---

공식 [`#dim` / `#redim` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm)를 참조하세요.
