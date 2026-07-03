# [Setup]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=setupsection){ .md-button
.md-button--primary }

`[Setup]` 섹션은 모든 Inno Setup 스크립트의 골격입니다. 간단한 `Directive=Value` 형식을 사용하며 Windows 프로그램 추가/제거에 표시되는 애플리케이션 메타데이터부터 압축 알고리즘,
마법사 모양, 권한 요구사항까지 모든 것을 제어합니다. `AppName`과 `AppVersion`만 엄격히 필수이며, 다른 모든 지시문에는 적절한 기본값이 있습니다.

---

**애플리케이션 식별**

## AppName

`string` · **필수**

애플리케이션의 전체 표시 이름. 설치 마법사 전체와 Windows 프로그램 추가/제거 목록에 표시됩니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_appname)

---

## AppVersion

`string` · **필수**

애플리케이션의 버전 문자열(예: `1.0` 또는 `2.3.1`). 프로그램 추가/제거에 표시되며 업그레이드 감지 로직에 사용됩니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_appversion)

---

## AppPublisher

`string`

프로그램 추가/제거에 표시되는 게시자 이름.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisher)

---

## AppPublisherURL

`string`

프로그램 추가/제거에서 클릭 가능한 링크로 표시되는 게시자 웹사이트 URL.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisherurl)

---

## AppSupportURL

`string`

프로그램 추가/제거에 표시되는 지원 URL.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_appsupporturl)

---

## AppUpdatesURL

`string`

프로그램 추가/제거에 표시되는 업데이트 또는 다운로드 URL.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_appupdatesurl)

---

**컴파일러 출력**

## Compression

`string`

설치 프로그램 페이로드를 압축하는 데 사용되는 압축 알고리즘. 유효한 값: `lzma2/ultra64`(기본값, 최고 압축률), `bzip2`, `deflate`, `none`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_compression)

---

## OutputDir

`string`

컴파일된 Setup EXE가 작성되는 디렉토리(스크립트 파일 기준 상대 경로). 기본값은 `Output`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_outputdir)

---

## OutputBaseFilename

`string`

컴파일된 설치 프로그램의 기본 파일 이름(`.exe` 제외), 예: `myapp-setup`. 기본값은 `setup`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_outputbasefilename)

---

## SourceDir

`string`

`[Files]` 및 기타 섹션에서 사용되는 상대 경로의 기본 디렉토리. 기본값은 스크립트가 포함된 디렉토리.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_sourcedir)

---

## SetupIconFile

`string`

컴파일된 Setup EXE의 아이콘으로 사용되는 `.ico` 파일 경로.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_setupiconfile)

---

## EncryptionKey

`string`

설치 프로그램 파일을 암호화하는 데 사용되는 비밀번호. 설치 프로그램은 런타임에 사용자에게 이 비밀번호를 묻습니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_encryptionkey)

---

**설치 위치**

## DefaultDirName

`string`

대상 선택 페이지에 표시되는 기본 설치 디렉토리(예: `{autopf}\MyApp`). 모든 Inno Setup 디렉토리 상수를 지원합니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultdirname)

---

## DefaultGroupName

`string`

시작 메뉴 폴더 선택 페이지에 표시되는 기본 시작 메뉴 폴더 이름.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultgroupname)

---

**권한**

## PrivilegesRequired

`string`

설치에 필요한 권한 수준. 유효한 값: `admin`(기본값), `lowest`, `none`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequired)

---

## PrivilegesRequiredOverridesAllowed

`string`

사용자가 필요한 권한 수준을 재정의할 수 있도록 합니다. 값: `commandline`, `dialog`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequiredoverridesallowed)

---

**Windows 버전 제약**

## MinVersion

`string`

설치 프로그램을 실행하는 데 필요한 최소 Windows 버전(예: Windows 7의 경우 `6.1`). `0`을 사용하면 설치를 효과적으로 비활성화합니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_minversion)

---

## ArchitecturesAllowed

`string` · **여러 값**

설치 프로그램이 실행될 수 있는 CPU 아키텍처의 공백으로 구분된 목록: `x86`, `x64`, `arm64`, `ia64`. 기본값은 모두.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesallowed)

---

## ArchitecturesInstallIn64BitMode

`string` · **여러 값**

설치 프로그램이 64비트 모드로 실행되는 아키텍처의 공백으로 구분된 목록(예: `x64compatible arm64`).

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesinstallin64bitmode)

---

**런타임 동작**

## AppMutex

`string`

설치 프로그램이 시작 전에 확인하는 뮤텍스 이름. 뮤텍스가 존재하면 애플리케이션이 이미 실행 중임을 사용자에게 경고합니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_appmutex)

---

## CloseApplications

`boolean`

`yes`이면 설치 프로그램이 업데이트를 위해 파일을 잠근 애플리케이션을 자동으로 닫으려고 시도합니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_closeapplications)

---

## LicenseFile

`string`

사용권 계약 마법사 페이지에 표시되는 `.txt` 또는 `.rtf` 라이선스 파일 경로.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_licensefile)

---

## InfoBeforeFile

`string`

설치 시작 전 정보 페이지에 표시되는 `.txt` 또는 `.rtf` 파일 경로.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_infobeforefile)

---

## InfoAfterFile

`string`

설치 완료 후 정보 페이지에 표시되는 `.txt` 또는 `.rtf` 파일 경로.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_infoafterfile)

---

**제거 프로그램**

## AllowNoIcons

`boolean`

`yes`이면 시작 메뉴 폴더 선택 페이지에 *시작 메뉴 폴더를 만들지 않음* 체크박스가 나타납니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_allownoicons)

---

## UninstallDisplayName

`string`

프로그램 추가/제거에서 애플리케이션에 표시되는 이름. 기본값은 `AppName`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayname)

---

## UninstallDisplayIcon

`string`

프로그램 추가/제거에서 아이콘으로 사용되는 EXE, DLL 또는 ICO 경로(예: `{app}\MyApp.exe`).

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayicon)

---

## CreateUninstallRegKey

`boolean`

`HKLM\Software\Microsoft\Windows\CurrentVersion\Uninstall` 아래에 제거 레지스트리 키를 생성할지 여부를 제어합니다. 기본값은 `yes`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_createuninstallregkey)

---

## Uninstallable

`string`

제거 프로그램 항목 생성 여부를 제어하는 Pascal 식 또는 `yes`/`no` 값. 기본값은 `yes`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstallable)

---

**마법사 페이지**

## DisableDirPage

`string`

대상 디렉토리 선택 페이지의 표시 여부를 제어합니다. 유효한 값: `yes`, `no`, `auto`(기본값).

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_disabledirpage)

---

## DisableProgramGroupPage

`string`

시작 메뉴 폴더 선택 페이지의 표시 여부를 제어합니다. 유효한 값: `yes`, `no`, `auto`(기본값).

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_disableprogramgrouppage)

---

## DisableWelcomePage

`boolean`

`yes`이면 환영 페이지를 완전히 건너뜁니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_disablewelcomepage)

---

## DisableReadyPage

`boolean`

`yes`이면 설치 준비 페이지를 완전히 건너뜁니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_disablereadypage)

---

## UsePreviousAppDir

`boolean`

`yes`이면 재설치 시 이전에 사용한 설치 디렉토리가 미리 채워집니다. 기본값은 `yes`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousappdir)

---

## UsePreviousGroup

`boolean`

`yes`이면 재설치 시 이전에 사용한 시작 메뉴 폴더 이름이 미리 채워집니다. 기본값은 `yes`.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousgroup)

---

## UserInfoPage

`boolean`

`yes`이면 사용자 이름, 회사 이름, 선택적 일련번호를 묻는 페이지가 표시됩니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_userinfopage)

---

**마법사 모양**

## WizardStyle

`string`

마법사의 시각적 스타일. 유효한 값: `classic`, `modern`(기본값).

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardstyle)

---

## WizardResizable

`boolean`

`yes`이면 사용자가 마법사 창 크기를 조정할 수 있습니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardresizable)

---

## WizardImageFile

`string`

클래식 스타일에서 마법사 왼쪽에 표시되는 BMP 파일 경로.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardimagefile)

---

## WizardSmallImageFile

`string`

마법사 오른쪽 상단 모서리에 표시되는 BMP 파일 경로.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardsmallimagefile)

---

## SetupLogging

`boolean`

`yes`이면 설치 중에 사용자의 임시 디렉토리에 로그 파일이 자동으로 생성됩니다.

[:octicons-link-external-16: 참조](https://jrsoftware.org/ishelp/index.php?topic=setup_setuplogging)
