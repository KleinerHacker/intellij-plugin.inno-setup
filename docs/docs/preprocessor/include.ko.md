# `#include` / `#file`

`#include`는 컴파일 시 다른 파일의 내용을 스크립트에 붙여넣고, `#file`은 파일을 읽어 그 내용을 전처리기가
사용할 수 있게 합니다. 둘 다 외부 내용을 스크립트로 가져옵니다.

---

## 구문

```ini
#include "filename.iss"
#include <filename.iss>
#file "data.txt"
```

- `#include "file"`은 포함하는 스크립트를 기준으로 상대적으로 해석됩니다(절대 경로는 그대로 사용). 꺾쇠
  형식은 구성된 포함 경로를 검색합니다.
- `#file "file"`은 파일을 읽고 다른 지시문이 참조할 수 있는 임시 파일 이름을 반환합니다.

---

## `#include` 작업

플러그인은 `#include`를 일급 참조로 취급합니다:

- **파일로 이동** — **Ctrl+B** / **Cmd+B**로 참조된 파일로 점프하며, 입력하는 동안 경로가 완성됩니다.
- **자동 경로 업데이트** — IDE에서 대상 파일의 이름을 바꾸거나 옮기면 `#include` 경로가 업데이트됩니다.
- **`#include` 내용 인라인화** — `#include` 줄에서 **Alt+Enter** 인텐션이 해당 줄을 파일의 내용(한 단계만)
  으로 바꿉니다. 이후 인라인된 파일을 삭제할지 묻습니다.
- **선택 영역을 `#include` 파일로 추출** — 줄을 선택하여 현재 스크립트 옆의 새 파일로 옮기고, 선택 영역은
  새 파일의 `#include`로 대체됩니다.
- **유효 스크립트 표시** — 포함이 완전히 해석된 스크립트를 읽기 전용 탭으로 엽니다.

```ini
#include "common\\settings.iss"

[Setup]
AppName={#MyAppName}      ; 포함된 파일 내부에서 정의됨
```

---

## 검증

`#include` 줄 자체가 검사됩니다. 누락되거나 존재하지 않는 파일, 리터럴이 아니거나 빈 경로는 오류로
표시됩니다. 포함된 파일 **내부**에서 감지된 문제(알 수 없는 지시문, 플래그, 정의되지 않은 상수)는
포함하는 스크립트의 `#include` 줄에 표시되며, 필수 섹션 검사도 포함이 기여하는 내용을 고려합니다.

---

공식
[`#include` :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_include.htm) 및
[`#file` :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_file.htm) 문서.
