<p align="center">
  <img src="docs/docs/assets/images/inno-setup-logo.png" alt="Inno Setup Logo" width="256"/>
</p>

<p align="center">
  <a href="README.md">English</a> ·
  <a href="README.zh.md">简体中文</a> ·
  <a href="README.ja.md">日本語</a> ·
  <b>한국어</b>
</p>

# Inno Setup – JetBrains 플러그인

전체 IntelliJ 플랫폼 제품군에 [Inno Setup](https://jrsoftware.org/isinfo.php) 스크립트(`.iss`)에 대한
최고 수준의 언어 지원을 제공하는 JetBrains IDE 플러그인입니다.

---

## 소개

[Inno Setup](https://jrsoftware.org/isinfo.php)은 Jordan Russell과 Martijn Laan이 개발한, 널리 사용되는
무료 Windows 설치 프로그램 빌더입니다(1997년 최초 릴리스). 그 스크립트(`.iss`)는 파일, 레지스트리 키,
바로 가기, 그리고 선택적 Pascal 스크립팅 등 설치 프로그램 전체를 기술하지만, 지금까지 JetBrains IDE에는
전용 에디터 지원이 없었습니다.

이 플러그인은 그 공백을 메웁니다. 목표는 어떤 JetBrains IDE를 사용하든 올바른 강조 표시, 컨텍스트 인식
자동 완성, 인라인 문서, 검증된 참조 등 `.iss` 파일에 대한 완전한 편집 경험을 제공하는 것입니다.

### 기능

| 기능                   | 설명                                                                                                          |
|------------------------|---------------------------------------------------------------------------------------------------------------|
| **구문 강조**          | 섹션, 지시문, 매개변수, 상수(`{app}`, `{autopf}` …), Pascal 코드 블록이 서로 다른 색상으로 표시됩니다         |
| **코드 자동 완성**     | 입력하는 동안 섹션 이름, 지시문 키, 매개변수 키, 알려진 플래그 값이 제안됩니다                                 |
| **인라인 문서**        | 지시문이나 매개변수에 마우스를 올리면 IDE를 벗어나지 않고 설명을 읽을 수 있습니다                              |
| **참조 해석**          | `Name:` 선언과 `Tasks:`, `Components:`, `Types:` 매개변수에서의 사용 사이를 이동합니다                        |
| **구조 뷰**            | 모든 섹션과 그 항목을 한눈에 조망합니다                                                                        |
| **상수 검증**          | 따옴표로 묶인 문자열 안에 포함된 상수를 포함하여 내장 상수를 인식하고 검증합니다                               |
| **괄호/따옴표 매칭**   | `{`, `[`, `"` 를 자동으로 닫습니다                                                                             |
| **코드 접기**          | 섹션, 긴 매개변수 항목, `#if … #endif` 블록을 개별적으로 접을 수 있습니다                                      |
| **인라인 힌트**        | `Languages:` 매개변수 값 옆에 언어 플래그 아이콘이 인라인으로 표시됩니다                                       |
| **빌드 통합**          | 컨텍스트 메뉴 동작으로 `.iss` 스크립트를 직접 컴파일하며, 선택적으로 프로젝트 빌드 시 ISCC를 자동 실행합니다   |
| **언어 파일 지원**     | `.isl` 언어 파일이 `.iss` 스크립트와 함께 인식·강조·검증됩니다                                                 |
| **ISPP 지원**          | 전처리기 지시문(스코프 키워드가 있는 `#define`/`#undef`, `#include`, `#if`/`#elif`/`#else`/`#endif` …)이 파싱·강조·완성·검증·참조 해석됩니다 |

### IDE 호환성

이 플러그인은 모든 완전한 IntelliJ 플랫폼 IDE에서 사용할 수 있는 `com.intellij.modules.lang`을 대상으로 하며,
자체 런타임 종속성을 번들로 포함하므로 호스트 IDE에 대한 숨겨진 요구 사항이 없습니다.

지원 IDE: **IntelliJ IDEA**, **PyCharm**, **CLion / CLion Nova**, **Rider**, **WebStorm**, **GoLand**,
**RubyMine**, **DataGrip** 및 기타 모든 IntelliJ 플랫폼 IDE.

---

## 시작하기 (개발)

### 사전 요구 사항

| 도구          | 버전                                          |
|---------------|-----------------------------------------------|
| JDK           | 21 이상                                       |
| IntelliJ IDEA | 2024.1 이상 (IDE 지원 개발용)                 |
| Gradle        | Gradle Wrapper로 제공 — 설치 불필요           |

### 빌드

```bash
# 저장소 복제
git clone https://github.com/KleinerHacker/inno-setup.git
cd inno-setup

# 파서/렉서를 생성하고 모든 모듈을 컴파일
./gradlew assemble

# 모든 테스트 실행 (:plugin 모듈에 있음)
./gradlew :plugin:test

# 배포 가능한 플러그인 ZIP 빌드
./gradlew :plugin:buildPlugin
# → plugin/build/distributions/inno-setup-<version>.zip
```

### 샌드박스 IDE에서 실행

```bash
./gradlew runIde
```

이 명령은 플러그인이 로드된 새 IntelliJ IDEA 인스턴스를 일반 IDE 설치와 격리된 상태로 실행합니다. 아무 `.iss`
파일을 열거나 만들어 플러그인을 실시간으로 사용해 보세요.

### IntelliJ IDEA에서 실행 / 디버그

`.run/`에 미리 구성된 실행 구성이 포함되어 있습니다:

| 구성                  | 하는 일                                                     |
|-----------------------|-------------------------------------------------------------|
| **Run Plugin**        | `:runIde` 실행 — 플러그인이 포함된 샌드박스 IDE를 엽니다     |
| **Run Tests**         | `:test` 실행                                                |
| **Run Verifications** | `:verifyPlugin` 실행하여 호환성 확인                        |

### 프로젝트 구조

의존성 체인이 `:plugin → :language:script → :language:preprocessor`인 **Gradle 멀티 모듈** 빌드입니다.
루트 프로젝트는 순수 애그리게이터입니다(코드 없음, `plugin.xml` 없음).

```
.
├── language/
│   ├── preprocessor/        ISPP 전처리기 언어 (렉서/파서/PSI, 하이라이터, 어노테이터,
│   │                        괄호 매처, 참조, 표현식 엔진, ISPP 스펙, PluginBundle)
│   │   └── src/main/{kotlin, resources/{META-INF, parsing, spec, messages}}
│   └── script/              Inno Setup 언어: 섹션/INI 문법 (.iss/.isl/.ist), 파일 형식,
│       │                    하이라이터, 접기, 어노테이터, 참조, include 인프라, ISPP 인젝터,
│       │                    spec/settings 서비스
│       └── src/main/{kotlin, resources/{META-INF, parsing, spec, icons}}
├── plugin/                  배포 가능한 플러그인: IDE 기능, 빌드/실행, 설정 UI, 메인 plugin.xml,
│   │                        색상 구성표, 아이콘 — 그리고 모든 테스트
│   └── src/{main, test}/
├── buildSrc/                공유 Gradle 규약 (inno-setup.platform-module)
├── <module>/build/generated/  모듈별로 생성된 파서/렉서/PSI (자동 생성)
├── docs/                    MkDocs 문서 사이트
├── build.gradle.kts         루트 애그리게이터 (전체 모듈 Dokka, kover 병합, MkDocs, generateSources)
└── settings.gradle.kts
```

> **참고:** 생성된 소스는 모듈별로 `<module>/build/generated/`에 위치합니다. `./gradlew generateSources`
> (루트 통합 태스크) 또는 모듈별 `generateIs*Parser`/`generateIs*Lexer` 태스크로 다시 생성하세요.
> 절대 수동으로 편집하지 마세요 — 빌드할 때마다 덮어써집니다.

---

## 수동 설치

이 플러그인은 **아직 JetBrains Marketplace에서 제공되지 않습니다**. 빌드된 ZIP에서 수동으로 설치하세요:

### 1단계 — 플러그인 ZIP 빌드

```bash
./gradlew buildPlugin
```

출력은 `build/distributions/inno-setup-<version>.zip`에 기록됩니다.

### 2단계 — IDE에 설치

1. JetBrains IDE를 열고 **Settings / Preferences → Plugins**로 이동합니다
2. Plugins 패널 오른쪽 상단의 **⚙ 톱니바퀴 아이콘**을 클릭합니다
3. **Install Plugin from Disk…**를 선택합니다
4. `build/distributions/`로 이동하여 `.zip` 파일을 선택합니다
5. **OK**를 클릭한 후 메시지가 표시되면 **IDE를 다시 시작**합니다

다시 시작한 후에는 `.iss` 확장자를 가진 모든 파일이 자동으로 플러그인에 의해 처리됩니다.

---

## 문서

[전체 문서](https://kleinerhacker.github.io/intellij-plugin.inno-setup/) — 각 Inno Setup 섹션과 그 매개변수에
대한 완전한 참조 포함 — 는 GitHub Pages에서 호스팅되는 프로젝트의 MkDocs 사이트에서 이용할 수 있습니다.

문서 사이트를 로컬에서 실행하려면:

```bash
# 종속성 설치 (한 번만)
cd docs
pip install mkdocs mkdocs-material

# 로컬에서 제공
mkdocs serve
```

그런 다음 브라우저에서 [http://127.0.0.1:8000](http://127.0.0.1:8000)을 엽니다.

> [API 문서](https://kleinerhacker.github.io/intellij-plugin.inno-setup/dokka/html/)도 이용할 수 있습니다.

---

## 기여

[버그 신고](https://github.com/KleinerHacker/intellij-plugin.inno-setup/issues)와
[풀 리퀘스트](https://github.com/KleinerHacker/intellij-plugin.inno-setup/pulls)를 환영합니다. 더 큰 변경 사항의
경우 먼저 issue를 열어 논의해 주세요.

---

## 라이선스

자세한 내용은 [LICENSES](https://kleinerhacker.github.io/intellij-plugin.inno-setup/licences/)를 참조하세요.
