# `#emit` / `#expr` / `#insert` / `#append`

이 지시문들은 식을 평가하고 그 결과를 어떻게 처리할지 결정합니다. 인라인 `{#…}` 형식의 명시적인
대응물입니다.

---

## 구문

```ini
#emit Expression
#expr Expression
#insert Expression
#append Expression
```

---

## 설명

- **`#emit`**은 식을 평가하여 그 값을 스크립트 한 줄로 출력에 씁니다. 일반 줄 내의 인라인 형식 `{#expr}`은
  `{#emit expr}`의 약식입니다.
- **`#expr`**은 식을 **부작용**만을 위해 평가하고(예: 함수 호출이나 매크로 할당) 결과는 버립니다 —
  아무것도 출력되지 않습니다.
- **`#insert`**와 **`#append`**는 현재 출력 줄을 기준으로 선택한 위치에 출력을 배치합니다 — `#insert`는 그
  앞에, `#append`는 그 뒤에 — 섹션을 프로그래밍적으로 생성할 때 유용합니다.

```ini
#define AppExe "MyApp.exe"

[Run]
#emit "Filename: ""{app}\\" + AppExe + """; Flags: nowait"

; 부작용을 위해 평가하고 아무것도 출력하지 않음
#expr Local[0] = GetEnv("BUILD_ID")
```

---

## 편집기 지원

네 가지 지시문 키워드 모두 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해 검증되고, 식은 구문
분석되고 형식이 검사됩니다.

---

공식 [`#emit` / `#expr` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_emit.htm).
