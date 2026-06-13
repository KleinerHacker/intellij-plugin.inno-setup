# [Icons]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=iconssection){ .md-button .md-button--primary }

`[Icons]` 섹션은 설치 중에 Windows 바로 가기를 만듭니다——시작 메뉴, 바탕화면 또는 다른 곳에. 각 항목은 정확히 하나의 바로 가기를 만듭니다. 대상은 실행 파일, 문서, 폴더 또는 URL이 될 수 있습니다. `Tasks` 매개변수를 사용하여 바로 가기를 선택 사항으로 만들어 사용자가 *추가 작업 선택* 마법사 페이지에서 결정할 수 있도록 합니다.

---

## Name

`string` · **필수**

바로 가기의 전체 경로와 이름(예: `{group}\My Program` 또는 `{commondesktop}\My Program`).

---

## Filename

`string` · **필수**

바로 가기의 대상——실행 파일, 문서, 폴더 또는 URL.

---

## Parameters

`string`

바로 가기가 활성화될 때 대상에 전달되는 명령줄 인수.

---

## WorkingDir

`string`

바로 가기가 실행될 때 설정되는 작업 디렉토리. 기본값은 대상이 포함된 디렉토리.

---

## HotKey

`string`

대상을 실행하는 전역 키보드 단축키(예: `ctrl+alt+k`).

---

## Comment

`string`

사용자가 바로 가기 위에 마우스를 올릴 때 표시되는 툴팁 텍스트.

---

## IconFilename

`string`

이 바로 가기의 아이콘을 포함하는 `.ico`, `.exe` 또는 `.dll` 경로.

---

## IconIndex

`integer`

`IconFilename` 내 아이콘의 0부터 시작하는 인덱스. 기본값은 `0`.

---

## AppUserModelID

`string`

Windows 7+ 애플리케이션 사용자 모델 ID. 작업 표시줄 버튼 그룹화 및 토스트 알림 연결에 사용됩니다.

---

## AppUserModelToastActivatorCLSID

`string` · **6.1 이상**

이 바로 가기를 통한 토스트 알림 활성화를 위한 Windows 10+ COM CLSID.

---

## Flags

`string` · **여러 값**

동작 플래그: `runminimized`, `runmaximized`, `closeonexit`, `createonlyiffileexists`, `preventpinning`.

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

이 바로 가기가 생성되기 직전에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## AfterInstall

`string`

이 바로 가기가 생성된 직후에 호출되는 `[Code]`의 Pascal 프로시저 이름.

---

## MinVersion

`string`

이 항목이 적용되는 최소 Windows 버전. `0`을 사용하면 적용되지 않습니다.

---

## OnlyBelowVersion

`string`

이 항목이 적용되는 최대 Windows 버전(제외). `0`을 사용하면 상한 없음.
