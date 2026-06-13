# [Setup]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setupsection){
.md-button .md-button--primary }

`[Setup]` セクションはすべての Inno Setup スクリプトの骨格です。シンプルな `Directive=Value` 形式を使用し、Windows の追加と削除に表示されるアプリケーションメタデータから圧縮アルゴリズム、ウィザードの外観、権限要件まですべてを制御します。厳密に必須なのは `AppName` と `AppVersion` のみで、他のすべてのディレクティブには適切なデフォルト値があります。

---

**アプリケーション識別情報**

## AppName

`string` · **必須**

アプリケーションの完全な表示名。インストールウィザード全体と Windows の追加と削除リストに表示されます。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_appname)

---

## AppVersion

`string` · **必須**

アプリケーションのバージョン文字列（例：`1.0` または `2.3.1`）。追加と削除に表示され、アップグレード検出ロジックに使用されます。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_appversion)

---

## AppPublisher

`string`

追加と削除に表示される発行者名。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisher)

---

## AppPublisherURL

`string`

追加と削除でクリック可能なリンクとして表示される発行者のウェブサイト URL。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_apppublisherurl)

---

## AppSupportURL

`string`

追加と削除に表示されるサポート URL。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_appsupporturl)

---

## AppUpdatesURL

`string`

追加と削除に表示される更新またはダウンロード URL。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_appupdatesurl)

---

**コンパイラー出力**

## Compression

`string`

インストーラーのペイロードをパックするために使用する圧縮アルゴリズム。有効な値：`lzma2/ultra64`（デフォルト、最高圧縮率）、`bzip2`、`deflate`、`none`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_compression)

---

## OutputDir

`string`

コンパイルされた Setup EXE が書き込まれるディレクトリ（スクリプトファイルからの相対パス）。デフォルトは `Output`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_outputdir)

---

## OutputBaseFilename

`string`

コンパイルされたインストーラーのベースファイル名（`.exe` なし）、例：`myapp-setup`。デフォルトは `setup`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_outputbasefilename)

---

## SourceDir

`string`

`[Files]` と他のセクションで使用される相対パスのベースディレクトリ。デフォルトはスクリプトを含むディレクトリ。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_sourcedir)

---

## SetupIconFile

`string`

コンパイルされた Setup EXE のアイコンとして使用される `.ico` ファイルへのパス。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_setupiconfile)

---

## EncryptionKey

`string`

インストーラーのファイルを暗号化するために使用するパスワード。インストーラーは実行時にユーザーにこのパスワードの入力を求めます。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_encryptionkey)

---

**インストール場所**

## DefaultDirName

`string`

インストール先の選択ページに表示されるデフォルトのインストールディレクトリ（例：`{autopf}\MyApp`）。すべての Inno Setup ディレクトリ定数をサポートします。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultdirname)

---

## DefaultGroupName

`string`

スタートメニューフォルダーの選択ページに表示されるデフォルトのスタートメニューフォルダー名。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_defaultgroupname)

---

**権限**

## PrivilegesRequired

`string`

インストールに必要な権限レベル。有効な値：`admin`（デフォルト）、`lowest`、`none`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequired)

---

## PrivilegesRequiredOverridesAllowed

`string`

ユーザーが必要な権限レベルをオーバーライドできるようにします。値：`commandline`、`dialog`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_privilegesrequiredoverridesallowed)

---

**Windows バージョン制約**

## MinVersion

`string`

インストーラーを実行するために必要な最低 Windows バージョン（例：Windows 7 の場合は `6.1`）。`0` を使用するとインストールを事実上無効にします。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_minversion)

---

## ArchitecturesAllowed

`string` · **複数の値**

インストーラーが実行できる CPU アーキテクチャのスペース区切りリスト：`x86`、`x64`、`arm64`、`ia64`。デフォルトはすべて。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesallowed)

---

## ArchitecturesInstallIn64BitMode

`string` · **複数の値**

インストーラーが 64 ビットモードで実行されるアーキテクチャのスペース区切りリスト（例：`x64compatible arm64`）。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_architecturesinstallin64bitmode)

---

**ランタイム動作**

## AppMutex

`string`

インストーラーが起動前にチェックするミューテックス名。ミューテックスが存在する場合、アプリケーションがすでに実行中であることをユーザーに警告します。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_appmutex)

---

## CloseApplications

`boolean`

`yes` の場合、インストーラーは更新のためにファイルをロックしているアプリケーションを自動的に閉じようとします。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_closeapplications)

---

## LicenseFile

`string`

使用許諾契約ウィザードページに表示される `.txt` または `.rtf` ライセンスファイルへのパス。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_licensefile)

---

## InfoBeforeFile

`string`

インストール開始前の情報ページに表示される `.txt` または `.rtf` ファイルへのパス。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_infobeforefile)

---

## InfoAfterFile

`string`

インストール完了後の情報ページに表示される `.txt` または `.rtf` ファイルへのパス。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_infoafterfile)

---

**アンインストーラー**

## AllowNoIcons

`boolean`

`yes` の場合、スタートメニューフォルダーの選択ページに*スタートメニューフォルダーを作成しない*チェックボックスが表示されます。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_allownoicons)

---

## UninstallDisplayName

`string`

追加と削除でアプリケーションに表示される名前。デフォルトは `AppName`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayname)

---

## UninstallDisplayIcon

`string`

追加と削除でアイコンとして使用される EXE、DLL、または ICO へのパス（例：`{app}\MyApp.exe`）。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstalldisplayicon)

---

## CreateUninstallRegKey

`boolean`

`HKLM\Software\Microsoft\Windows\CurrentVersion\Uninstall` の下にアンインストールレジストリキーを作成するかどうかを制御します。デフォルトは `yes`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_createuninstallregkey)

---

## Uninstallable

`string`

アンインストーラーエントリーを作成するかどうかを制御する Pascal 式または `yes`/`no` 値。デフォルトは `yes`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_uninstallable)

---

**ウィザードページ**

## DisableDirPage

`string`

インストール先ディレクトリの選択ページの表示を制御します。有効な値：`yes`、`no`、`auto`（デフォルト）。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_disabledirpage)

---

## DisableProgramGroupPage

`string`

スタートメニューフォルダーの選択ページの表示を制御します。有効な値：`yes`、`no`、`auto`（デフォルト）。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_disableprogramgrouppage)

---

## DisableWelcomePage

`boolean`

`yes` の場合、ウェルカムページを完全にスキップします。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_disablewelcomepage)

---

## DisableReadyPage

`boolean`

`yes` の場合、インストール準備完了ページを完全にスキップします。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_disablereadypage)

---

## UsePreviousAppDir

`boolean`

`yes` の場合、再インストール時に以前使用したインストールディレクトリが事前入力されます。デフォルトは `yes`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousappdir)

---

## UsePreviousGroup

`boolean`

`yes` の場合、再インストール時に以前使用したスタートメニューフォルダー名が事前入力されます。デフォルトは `yes`。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_usepreviousgroup)

---

## UserInfoPage

`boolean`

`yes` の場合、ユーザーの名前、会社名、およびオプションのシリアル番号を尋ねるページが表示されます。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_userinfopage)

---

**ウィザードの外観**

## WizardStyle

`string`

ウィザードのビジュアルスタイル。有効な値：`classic`、`modern`（デフォルト）。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardstyle)

---

## WizardResizable

`boolean`

`yes` の場合、ユーザーはウィザードウィンドウのサイズを変更できます。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardresizable)

---

## WizardImageFile

`string`

クラシックスタイルでウィザードの左側に表示される BMP ファイルへのパス。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardimagefile)

---

## WizardSmallImageFile

`string`

ウィザードの右上隅に表示される BMP ファイルへのパス。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_wizardsmallimagefile)

---

## SetupLogging

`boolean`

`yes` の場合、インストール中にユーザーの一時ディレクトリにログファイルが自動的に作成されます。

[:octicons-link-external-16: リファレンス](https://jrsoftware.org/ishelp/index.php?topic=setup_setuplogging)
