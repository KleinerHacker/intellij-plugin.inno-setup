# [UninstallRun]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=uninstallrunsection){
.md-button .md-button--primary }

`[UninstallRun]` 섹션은 `[Run]`과 동일하게 작동하지만 항목은 설치 후가 아니라 제거 *시작* 시에 실행됩니다. 서비스 중지, 실행 중인 프로세스 종료, 또는 파일 및 레지스트리 키 삭제로는 처리할
수 없는 상태 정리에 사용합니다. 항목은 표시된 순서대로 실행됩니다.

---

## Filename

`string` · **필수**

제거 중에 실행할 실행 파일, 문서 또는 폴더 경로.

---

## Description

`string`

제거 후 페이지에 표시되는 선택적 체크박스의 레이블. `postinstall` 플래그가 필요합니다.

---

## Parameters

`string`

`Filename`에 전달되는 명령줄 인수.

---

## WorkingDir

`string`

실행된 프로세스의 작업 디렉토리. 기본값은 `Filename`이 포함된 디렉토리.

---

## StatusMsg

`string`

이 항목이 실행되는 동안 진행률 창에 표시되는 상태 메시지.

---

## RunOnceId

`string`

이 항목이 여러 번의 제거 실행에서 두 번 이상 실행되지 않도록 하는 고유 식별자.

---

## Verb

`string`

`shellexec` 플래그와 함께 사용되는 셸 동사(예: `open`, `print`).

---

## OnLog

`string` · **6.6 이상**

각 출력 줄에 대해 호출되는 `[Code]`의 Pascal 프로시저 이름(`logoutput` 플래그 필요).

---

## Flags

`string` · **여러 값**

동작 플래그: `postinstall`, `shellexec`, `nowait`, `runhidden`, `skipifsilent`, `skipifnotsilent`, `unchecked`,
`waituntilterminated`, `waituntilidle`, `logoutput`, `runasoriginaluser`.

---

## Components

`→ Components` · **여러 값**

나열된 컴포넌트 중 하나 이상이 선택된 경우에만 처리됩니다.

---

## Tasks

`→ Tasks` · **여러 값**

나열된 태스크 중 하나 이상이 체크된 경우에만 처리됩니다.

---

## Languages

`→ Languages` · **여러 값**

이 항목을 지정된 언어로 제한합니다.

---

## Check

`string`

`[Code]`에서 `Boolean`을 반환하는 Pascal 함수 이름. 함수가 `True`를 반환하는 경우에만 처리됩니다.

---

## BeforeInstall

`string`

이 항목이 처리되기 직전에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## AfterInstall

`string`

이 항목이 처리된 직후에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## MinVersion

`string`

이 항목이 적용되는 최소 Windows 버전. `0`을 사용하면 적용되지 않습니다.

---

## OnlyBelowVersion

`string`

이 항목이 적용되는 최대 Windows 버전(제외). `0`을 사용하면 상한 없음.
