# Inno Setup – JetBrains 플러그인

**모든 JetBrains IDE에서 Inno Setup 스크립트(`.iss`)와 언어 파일(`.isl`)을 최고 수준으로 지원합니다.**

---

## Inno Setup이란?

[Inno Setup](https://jrsoftware.org/isinfo.php)은 Jordan Russell과 Martijn Laan이 만든 무료 오픈소스 Windows 설치 프로그램 빌더입니다. 1997년에
처음 출시된 이후 Windows 생태계에서 가장 널리 사용되는 설치 도구 중 하나로 성장했으며, **Visual Studio Code**, **Git for Windows**, **Embarcadero Delphi
** 등의 프로젝트 설치 프로그램을 지원합니다.

Inno Setup 스크립트(`.iss`)는 전체 설치 구성을 설명합니다: 설치할 파일, 생성할 레지스트리 키, 추가할 바로 가기, 설치 마법사의 동작 방식. 언어 파일(`.isl`)은 현지화된 마법사 텍스트와 언어
메타데이터를 제공합니다. 함께 다양한 섹션, 매개변수, 메시지, 그리고 완전한 런타임 사용자 지정을 위한 통합 Pascal 스크립팅 엔진을 지원합니다.

!!! tip "공식 Inno Setup 리소스"

- :octicons-home-16: [홈페이지](https://jrsoftware.org/isinfo.php)
- :octicons-book-16: [문서](https://jrsoftware.org/ishelp/)
- :octicons-download-16: [다운로드](https://jrsoftware.org/isdl.php)

---

## 이 플러그인이 하는 일

이 플러그인은 **IntelliJ IDEA**, **PyCharm**, **CLion**, **Rider**, **WebStorm**, **GoLand** 등 모든 JetBrains IDE에서 `.iss` 및
`.isl` 파일에 대한 최고 수준의 편집기 지원을 제공합니다:

- **구문 강조** — 섹션, 지시문, 매개변수, 값, 상수, Pascal 코드 블록을 각각 다른 색으로 표시
- **코드 완성** — 섹션 헤더, 매개변수 이름, 알려진 값을 입력하는 동안 제안
- **인라인 문서** — 지시문이나 매개변수 위에 마우스를 올리면 IDE를 떠나지 않고 설명 확인
- **참조 해결** — 스크립트 전체에서 컴포넌트, 태스크, 타입 정의 간 이동
- **현지화 지원** — `[Messages]`, `[CustomMessages]`, `[LangOptions]`, 언어 접두사, `{cm:...}` 참조 완전 지원
- **언어 메타데이터** — Windows LCID 완성, 내장 Inno Setup 언어 제안, 언어 참조용 국기 인레이
- **구조 뷰** — 프로젝트 도구 창에서 모든 섹션과 항목을 한눈에 확인
- **탐색 경로 및 고정 줄** — 탐색 경로 표시줄에 커서 위치까지의 경로(`setup.iss › [Files] › Source`)가 표시되고,
  스크롤하는 동안 현재 섹션 헤더가 편집기 상단에 고정됩니다
- **상수 지원** — `{app}`, `{autopf}`, `{group}` 및 기타 모든 내장 상수를 따옴표 문자열 내부 포함하여 인식 및 검증

---

## IDE 호환성

이 플러그인은 모든 완전한 JetBrains IDE에 존재하는 언어 지원 모듈 `com.intellij.modules.lang`에 대해 빌드되었습니다. IntelliJ IDEA를 특별히 필요로 하지 않으며, 자체
YAML 파싱 인프라를 포함하므로 호스트 IDE에 숨겨진 런타임 의존성이 없습니다.

| IDE                                  | 지원 여부 |
|--------------------------------------|-------|
| IntelliJ IDEA (Community & Ultimate) | ✔     |
| PyCharm (Community & Professional)   | ✔     |
| CLion / CLion Nova                   | ✔     |
| Rider                                | ✔     |
| WebStorm                             | ✔     |
| GoLand                               | ✔     |
| RubyMine                             | ✔     |
| DataGrip                             | ✔     |
| 기타 IntelliJ 플랫폼 IDE                  | ✔     |

---

## 설치

이 플러그인은 **아직 JetBrains Marketplace에 게시되지 않았습니다**. 로컬에서 빌드한 JAR/ZIP에서 수동으로 설치하세요:

### 1 · 플러그인 빌드

```bash
./gradlew buildPlugin
```

배포 가능한 ZIP은 `build/distributions/`에 생성됩니다.

### 2 · IDE에 설치

1. **설정 / 기본 설정 → 플러그인** 열기
2. ⚙ 기어 아이콘을 클릭하고 **디스크에서 플러그인 설치…** 선택
3. `build/distributions/`에서 ZIP 파일 선택
4. 메시지가 표시되면 IDE 재시작

!!! note "Marketplace 등록"
JetBrains Marketplace 출시가 계획되어 있습니다. 출시 후에는 IDE 내장 플러그인 브라우저에서 직접 설치할 수 있습니다.

---

## 섹션 참조

위쪽 탐색에는 Inno Setup 스크립트의 섹션 참조와 타입 정보 및 공식 Inno Setup 문서 링크를 포함한 전용 `.isl` 언어 파일 참조가 포함되어 있습니다.
