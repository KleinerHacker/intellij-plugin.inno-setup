# `#for`

`#for`는 C 스타일 루프 헤더를 평가하여 지시문을 여러 번 반복합니다. 이는 전처리기에서 반복적인 스크립트
내용을 생성하는 방법입니다 — 예를 들어 [배열](arrays.md)의 항목당 하나의 항목을 생성합니다.

---

## 구문

```ini
#for {Init; Condition; Increment} Directive
```

헤더는 세미콜론으로 구분된 세 부분으로 구성됩니다: 초기화, 각 반복 전에 검사되는 조건, 각 반복 후에
평가되는 증분 — C의 `for` 루프와 정확히 같습니다.

---

## 설명

각 반복에서 뒤따르는 지시문이 실행됩니다. [`#emit`](output.md) 또는 [배열](arrays.md)과 결합하면 `#for`는
일련의 스크립트 줄을 생성합니다:

```ini
#dim Langs[3]
#define Langs[0] "en"
#define Langs[1] "de"
#define Langs[2] "fr"

[Languages]
#for {i = 0; i < 3; i++} \
  #emit "Name: """ + Langs[i] + """; MessagesFile: ""compiler:Languages\\" + Langs[i] + ".isl"""
```

백슬래시는 지시문을 다음 줄로 이어 줍니다(연속 문자 변경은 [`#pragma spansymbol`](pragma.md) 참조).

---

## 편집기 지원

지시문 키워드는 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해 검증됩니다.

---

공식 [`#for` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_for.htm).
