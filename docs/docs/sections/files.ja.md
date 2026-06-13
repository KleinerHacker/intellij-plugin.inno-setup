# [Files]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=filessection){ .md-button .md-button--primary }

`[Files]` セクションは、インストーラーがターゲットマシンにコピーすべきすべてのファイルを宣言する場所です。ワイルドカード、再帰的ディレクトリツリー、フォントインストール、.NET GAC 登録、ファイル整合性ハッシュ、さらにはインストール時にインターネットからファイルをダウンロードすることもサポートします。リストされたファイルに必要なディレクトリは自動的に作成されます——`[Dirs]` エントリーは不要です。

---

## Source

`string` · **必須**

ソースファイルのパスまたはワイルドカードパターン（例：`MyProg.exe` または `Plugins\*`）。`SourceDir` からの相対パス。

---

## DestDir

`string` · **必須**

ターゲットマシンの宛先ディレクトリ（例：`{app}` または `{sys}`）。すべての Inno Setup 定数をサポートします。

---

## DestName

`string`

ターゲットマシン上でファイルをリネームします。省略した場合、元のファイル名が保持されます。

---

## Excludes

`string`

ワイルドカード使用時に除外するファイル名パターンのカンマ区切りリスト（例：`*.pdb,*.log`）。

---

## ExternalSize

`integer`

外部ファイルのバイト単位のサイズ（`external` フラグと共に使用）。ウィザードページでの正確なディスク容量計算に使用されます。

---

## Attribs

`string` · **複数の値**

ファイルインストール後に設定するファイルシステム属性：`readonly`、`hidden`、`system`、`notcontentindexed`。

---

## Permissions

`string` · **複数の値**

インストールされたファイルに設定する ACL 権限（例：`users-modify`、`everyone-readexec`）。

---

## FontInstall

`string`

フォントファイルをインストールする際に使用するレジストリフォント名（例：`My Font (TrueType)`）。Windows でのフォント登録をトリガーします。

---

## StrongAssemblyName

`string`

グローバルアセンブリキャッシュ（GAC）登録用の .NET 厳密名アセンブリ名。

---

## Hash

`string` · **6.5 以降**

ソースファイルの期待される SHA-256 ハッシュ。Inno Setup はコンパイル時にハッシュを検証して偶発的なファイル破損を検出します。

---

## ISSigAllowedKeys

`string` · **複数の値** · **6.5 以降**

ファイルの `.issig` 署名を検証するために `[ISSigKeys]` から使用するキー識別子のカンマ区切りリスト。

---

## ExtractArchivePassword

`string` · **6.5 以降**

暗号化されたアーカイブのパスワード（`extractarchive` フラグと共に使用）。インストーラー内に暗号化されずに保存されます。

---

## DownloadISSigSource

`string` · **6.5 以降**

インストール時にダウンロードされるファイルの `.issig` 署名ファイルの URL。

---

## DownloadUserName

`string` · **6.5 以降**

認証されたファイルダウンロードの HTTP Basic 認証ユーザー名（`download` フラグが必要）。

---

## DownloadPassword

`string` · **6.5 以降**

認証されたファイルダウンロードの HTTP Basic 認証パスワード（`download` フラグが必要）。

---

## Flags

`string` · **複数の値**

動作フラグ：`32bit`、`64bit`、`comparetimestamp`、`confirmoverwrite`、`deleteafterinstall`、`dontcopy`、`download`、`external`、`extractarchive`、`ignoreversion`、`isreadme`、`nocompression`、`onlyifdoesntexist`、`recursesubdirs`、`createallsubdirs`、`regserver`、`regtypelib`、`restartreplace`、`sharedfile`。

---

## Components

`→ Components` · **複数の値**

リストされたコンポーネントの少なくとも 1 つが選択されている場合にのみ、このエントリーが処理されます。

---

## Tasks

`→ Tasks` · **複数の値**

リストされたタスクの少なくとも 1 つがチェックされている場合にのみ、このエントリーが処理されます。

---

## Languages

`→ Languages` · **複数の値**

このエントリーを指定された言語に限定します。

---

## Check

`string`

`[Code]` の `Boolean` を返す Pascal 関数名。関数が `True` を返す場合にのみ、このエントリーが処理されます。

---

## BeforeInstall

`string`

このファイルがインストールされる直前に呼び出される `[Code]` の Pascal プロシージャ名。

---

## AfterInstall

`string`

このファイルがインストールされた直後に呼び出される `[Code]` の Pascal プロシージャ名。

---

## MinVersion

`string`

このエントリーが適用される最低 Windows バージョン。`0` を使用すると適用しません。

---

## OnlyBelowVersion

`string`

このエントリーが適用される最高 Windows バージョン（排他的）。`0` を使用すると上限なし。
