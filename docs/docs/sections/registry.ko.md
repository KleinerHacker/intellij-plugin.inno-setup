# [Registry]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=registrysection){
.md-button .md-button--primary }

`[Registry]` 섹션은 설치 중에 Windows 레지스트리 키와 값을 생성, 수정 또는 삭제합니다. 모든 주요 레지스트리 값 유형을 지원하며 `uninsdeletekey` 및 `uninsdeletekeyifempty` 같은 플래그를 통해 제거 시 레지스트리 데이터 처리를 세밀하게 제어합니다. 32비트 및 64비트 레지스트리 보기를 명시적으로 대상으로 지정할 수 있습니다.

---

## Root

`string` · **필수**

레지스트리 루트 하이브: `HKCU`, `HKLM`, `HKCR`, `HKU`, `HKCC`, 또는 `HKA`(설치 모드에 따라 자동). `32` 또는 `64`를 추가하면 특정 레지스트리 보기가 강제됩니다(예: `HKLM64`).

---

## Subkey

`string` · **필수**

`Root`를 기준으로 한 레지스트리 키 경로(예: `Software\My Company\My App`).

---

## ValueType

`string`

쓸 레지스트리 값 유형: `none`(키만), `string`, `expandsz`, `multisz`, `dword`, `qword`, `binary`.

---

## ValueName

`string`

레지스트리 값의 이름. 비워두면 키의 기본값을 대상으로 합니다.

---

## ValueData

`string`

쓸 데이터. 기존 값에 추가하려면 `{olddata}`를 사용하고, `multisz` 값의 줄 구분 기호로 `{break}`를 사용합니다.

---

## Permissions

`string` · **여러 값**

키에 설정할 ACL 권한: `full`, `modify`, `read`.

---

## Flags

`string` · **여러 값**

동작 플래그: `createvalueifdoesntexist`, `deletekey`, `deletevalue`, `dontcreatekey`, `noerror`, `preservestringtype`, `uninsclearvalue`, `uninsdeletekey`, `uninsdeletekeyifempty`, `uninsdeletevalue`.

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
