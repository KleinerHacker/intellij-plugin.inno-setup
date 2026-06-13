# [Code]

[:octicons-link-external-16: Inno Setup 참조](https://jrsoftware.org/ishelp/index.php?topic=scriptintro){
.md-button .md-button--primary }

`[Code]` 섹션은 Inno Setup의 스크립팅 엔진의 모든 기능이 발휘되는 곳입니다. 다른 모든 섹션과 달리 `Key=Value` 또는 `Key: Value` 구문을 사용하지 않습니다——*RemObjects Pascal Script*를 사용하여 설치 프로그램이 런타임에 컴파일하고 실행하는 자유 형식의 Pascal 소스 코드를 포함합니다.

`InitializeSetup`, `NextButtonClick`, `CurStepChanged`, `PrepareToInstall` 같은 이벤트 함수를 통해 설치 마법사의 거의 모든 단계를 가로채고, 사용자 지정 검사 수행, 파일 다운로드, 사용자 지정 페이지 표시, 레지스트리 쓰기 등을 할 수 있습니다.

!!! info "매개변수 없음"
`[Code]` 섹션에는 구조화된 매개변수가 없습니다. 전체 내용이 Pascal 소스 코드입니다. 사용 가능한 모든 이벤트 함수, 내장 프로시저, 지원되는 Pascal 언어 기능을 포함한 전체 API 표면은 [Inno Setup 스크립팅 참조](https://jrsoftware.org/ishelp/index.php?topic=scriptintro)를 참조하세요.
