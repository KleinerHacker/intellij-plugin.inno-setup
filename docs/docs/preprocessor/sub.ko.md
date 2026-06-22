# `#sub` / `#endsub`

`#sub`는 **서브루틴** — 반복적으로 호출할 수 있는 명명된 지시문 블록 — 을 시작하고 `#endsub`로 끝냅니다.
서브루틴은 대개 [`#for`](for.md) 루프가 실행하는 본문으로 사용됩니다.

---

## 구문

```ini
#sub Name
  ; 지시문 …
#endsub
```

---

## 설명

`#sub Name`과 `#endsub` 사이의 모든 것은 `Name` 아래에 저장되어 서브루틴이 호출될 때마다 실행됩니다(예:
`#for`의 `Func` 매개변수를 통해). 서브루틴 내부에서는 현재 루프 변수를 사용할 수 있으므로 각 호출마다 다른
것을 출력할 수 있습니다:

```ini
#dim Files[2]
#define Files[0] "app.exe"
#define Files[1] "help.chm"

#sub EmitFile
  #emit "Source: """ + Files[i] + """; DestDir: ""{app}"""
#endsub

[Files]
#for {i = 0; i < 2; i++; EmitFile}
```

모든 `#sub`는 대응하는 `#endsub`로 닫아야 합니다.

---

## 편집기 지원

두 지시문 키워드 모두 강조 표시되고 완성되며(`#` 뒤) 내장 ISPP 사양에 대해 검증됩니다.

---

공식 [`#sub` 문서 :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_sub.htm).
