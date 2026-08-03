# `#define`

`#define` はプリプロセッサーマクロ——コンパイル時にスクリプトへ置き換えられる名前付きの値または式——を宣言します。最もよく使われる
ISPP ディレクティブであり、プラグインが完全なセマンティクス（参照解決、リネーム、使用箇所検索）でサポートするディレクティブです。

---

## 構文

```ini
#define [Scope] Name [Value]
#define [Scope] Name(Param1, Param2) Expression
```

- `#define Name Value` は定数マクロを定義します（値は省略可能で、その場合は *void* マクロになります）。
- `#define Name(params) Expression` は関数形式のマクロを定義します。プラグインは式の本体を持たない関数形式マクロをエラーとしてフラグ付けします。
- 名前の前にオプションの**スコープキーワード**（`public`、`protected`、`private`）を付けられます
  （例：`#define public MyAppVersion "1.5.0"`）。キーワードとしてハイライト・補完され、付けても付けなくても
  名前の解決と参照は同じように機能します。
- `#undef Name` は以前に定義されたマクロを削除します。

---

## マクロの使用：`{#Name}`

通常のスクリプト行内では、`{#Name}`（`{#emit Name}` の短縮形）がマクロの値を出力します：

```ini
#define MyAppVersion "1.5.0"

[Setup]
AppVersion={#MyAppVersion}
OutputBaseFilename=setup-{#MyAppVersion}
```

- `{#Name}` はその `#define` 宣言に解決されます——定義へ移動（**Ctrl+B** / **Cmd+B**）と使用箇所検索（**Alt+F7**
  ）が機能し、リネームは宣言とすべての使用箇所を同期させます。
- `{` の直後と `{#` の後の両方で補完に提示されます。
- 一度も使用されない `#define` はフラグ付けされ、削除のクイックフィックスが提供されます。

---

## `#define` における式と演算

`#define` の値は単なるリテラルではなく、完全な**式**です。ISPP は C/C++
風の式文法を用いてコンパイル時に評価し、プラグインはそれを解析・型チェック・ハイライトします。単一のリテラルは最も単純なケースであり、複数の値を組み合わせる場合は
**演算子**で結合する必要があります。

```ini
#define Major     1
#define Minor     5
#define Build     100
#define Version   Str(Major) + "." + Str(Minor)   ; 文字列連結
#define NextBuild (Build + 1)                      ; 算術、括弧でグループ化
#define OutputDir "Builds\\" + Version             ; 別のマクロとの連結
#define IsBeta    Build < 200                       ; 比較 → 整数 0/1
```

### 型システム

すべての式は次のいずれかの型を持ち、プラグインは演算を検証するためにそれを推論します：

| 型      | 生成元                            | 備考                                    |
|--------|--------------------------------|---------------------------------------|
| `int`  | 整数リテラル（`100`）、算術/比較/論理の結果      |                                       |
| `str`  | 文字列リテラル（`"x"`、`'x'`）、文字列を返す関数  | 単一引用符または二重引用符。二重化した引用符 `""` はリテラルの引用符 |
| `void` | **値を持たない** `#define`           | `int`（`0` として）と `str`（`""` として）の両方に互換 |
| `any`  | 未解決の参照、マクロ引数、未知の関数の結果、`{…}` 定数 | 型チェックを抑制——誤ったエラーを発生させない               |

### 演算子

ISPP は C/C++ 風の完全な演算子セットをサポートします。プラグインはすべての演算子トークンをハイライトし、以下の型規則を適用します。

| グループ   | 演算子                         | 被演算子の型                        | 結果                  |
|--------|-----------------------------|-------------------------------|---------------------|
| 算術     | `+` `-` `*` `/` `%`         | `int`（`+` は `str` + `str` も可） | `int`（連結の場合は `str`） |
| 文字列連結  | `+`                         | `str` + `str`                 | `str`               |
| 比較     | `<` `>` `<=` `>=` `==` `!=` | 両方 `int` **または**両方 `str`      | `int`（`0`/`1`）      |
| 論理     | `&&` `\|\|` `!`             | `int`                         | `int`               |
| ビット    | `&` `\|` `^` `~`            | `int`                         | `int`               |
| シフト    | `<<` `>>`                   | `int`                         | `int`               |
| 三項     | `cond ? a : b`              | 条件は `int`                     | 分岐の型                |
| 単項（前置） | `+` `-` `~` `!`             | `int`                         | `int`               |
| グループ化  | `( … )`                     | —                             | 内側の式の型              |
| カンマ    | `a , b`                     | —                             | 右側の被演算子の型           |

**優先順位**（高いほど強く結合、C/C++ に準拠）：
`( )` と関数呼び出し → 単項 `+ - ~ !` → `* / %` → `+ -` → `<< >>` → `< > <= >=` → `== !=` →
`&` → `^` → `|` → `&&` → `||` → `?:` → `,`。
迷ったら括弧を使ってください——`#define X 1 + 2 * 3` は `7`、`#define X (1 + 2) * 3` は `9` です。

### 関数呼び出しが型を供給する

`#define` の値は任意の ISPP **組み込み関数**を呼び出すことができ（公式の完全なセットがプラグインに同梱されています）、その戻り値の型が周囲の式に流れ込みます——例えば
`Str(...)` は `str`、`Int(...)` と `Power(...)` は `int`、`FileExists(...)` は `int` です。したがって `Str(Major) + "."`
は有効（`str` + `str`）ですが、`Str(Major) * 2` は拒否されます。

```ini
#define FullVer  GetFileVersionString("app.exe")      ; str
#define Padded   "v" + Str(Build)                       ; str + str → str
#define Doubled  Power(2, 10) * 2                        ; int * int → int
```

### 組み込み関数呼び出しはシグネチャで検証されます

組み込み関数の呼び出しは、同梱のシグネチャ（下記のリファレンス）に対して検証されます：

| 例                                   | 理由                                                     |
|--------------------------------------|----------------------------------------------------------|
| `#define X Copy("abc")`              | `Copy` は 3 個の引数を必要とします                        |
| `#define X Copy("abc", "x", 2)`      | `Index` は `int` 宣言ですが文字列が渡されています          |
| `#define X StringChange("lit", …)`   | 参照渡しパラメータ（`S: str*`）にはマクロ名が必要です      |
| `#define X Warning("x") + 1`         | `Warning` は値を返さないため式で使用できません             |
| `#define X NoSuchFunc(1)`            | 不明なプリプロセッサ関数                                  |

既定値を持つパラメータ（例：`Find(S, Substr, Index = 1)`）は省略できます。必須パラメータがすべて渡されていれば、
引数が少ない呼び出しも受け入れられます。

`Ident` または `Array` と表記されたパラメータ（`Defined(Ident)`、`TypeOf(Ident)`、`DimOf(Array)`）は値ではなく
シンボルの**名前**を受け取ります：引数は裸の識別子でなければなりません。`Defined`/`TypeOf` では未定義でも構いませんが、
`DimOf` では配列が存在している必要があり、添字なしで渡します。

```ini
#dim Langs[2]
#define HasDebug  Defined(DEBUG)   ; DEBUG は存在しなくてもよい
#define Count     DimOf(Langs)     ; Langs[0] ではなく配列そのもの
```

### 再帰的な参照解決

別のマクロへの参照は**そのマクロ**の型を取り、名前を通じて再帰的に解決されます——そのため、被演算子自体が `#define`
であっても型エラーが検出されます：

```ini
#define A "x"
#define B 5
#define C A * B     ; A は str、B は int → "str * int" が * にフラグ付けされる
```

解決は**後方**参照のみをたどります（マクロは前の行で既に宣言されている必要があります）。これにより、正しく記述されたスクリプトでは参照の循環が不可能になります。残存する循環（例：自己参照
`#define P P + 1`、または順序が乱れた相互参照）は安全に断ち切られます：参照は `any` に縮退するため、無限ループも誤ったエラーも発生しません。

### プラグインがエラーとしてフラグ付けするもの

各問題は、行全体ではなく**正確な原因トークン**を指す**エラー**として報告されます：

| 例                     | マークされるトークン | 理由                |
|-----------------------|------------|-------------------|
| `#define X "a" * "b"` | `*`        | 文字列被演算子に対する算術演算子  |
| `#define X 1 + "s"`   | `+`        | `+` での整数と文字列の混在   |
| `#define X "a" < 1`   | `<`        | 文字列と整数の比較         |
| `#define X -"s"`      | `"s"`      | 文字列被演算子に対する単項マイナス |
| `#define X 5 6`       | `6`        | 演算子のない 2 つの被演算子   |
| `#define X (1 + 2`    | `(`        | 括弧の不均衡            |

`any` の被演算子（未解決の参照、マクロ引数、未知の関数、`{…}` 定数）を含む式は、有効なスクリプトでの誤検出を避けるため、意図的にフラグ付け
**されません**。

### 関数形式マクロの本体

式の規則は関数形式マクロの本体にも適用されます。引数は `any` として扱われるため、型エラーを引き起こすことはありません：

```ini
#define Max(a, b) a > b ? a : b
#define Clamp(x)  x < 0 ? 0 : x
```

---

## 組み込み関数リファレンス

ISPP は `#define` 式の中で呼び出せる多数の**組み込み関数**
を提供します。プラグインには公式の完全なセットが同梱されており、各関数の戻り値の型は式の型チェッカーに供給され（上記参照）、補完にも提示されます。以下のリストは網羅的で、（公式
ISPP 関数インデックスに合わせて）アルファベット順に並んでいます。

!!! note "記法"
`Name: int*` / `Name: str*` と示された引数は**参照渡し**で、関数は渡された変数に書き戻します。末尾の `= value` はデフォルト値を持つ
**省略可能**な引数を表します。

| 関数                                                                                                 | 戻り値    | 説明                                                                                                           |
|----------------------------------------------------------------------------------------------------|--------|--------------------------------------------------------------------------------------------------------------|
| `AddBackslash(S: str): str`                                                                        | `str`  | S の末尾にバックスラッシュがなければ追加します。                                                                                    |
| `AddQuotes(S: str): str`                                                                           | `str`  | S に空白が含まれる場合、二重引用符で囲みます。                                                                                     |
| `ChangeFileExt(Filename: str, Extension: str): str`                                                | `str`  | Filename の拡張子を Extension に置き換えて返します。                                                                         |
| `ComparePackedVersion(Version1: int, Version2: int): int`                                          | `int`  | 2 つのパック（エンコード済み）バージョン番号を比較し、-1・0・1 を返します。                                                                    |
| `Copy(S: str, Index: int, Count: int): str`                                                        | `str`  | S の部分文字列を返します。Index は 1 始まりです。                                                                               |
| `CopyFile(ExistingFile: str, NewFile: str): int`                                                   | `int`  | コンパイル時に既存ファイルをコピーします。成功時は非ゼロを返します。                                                                           |
| `DecodeVer(Version: int): str`                                                                     | `str`  | パックされたバージョン番号をドット区切りのバージョン文字列に変換します。                                                                         |
| `Defined(Ident): int`                                                                              | `int`  | 識別子が定義済みなら 1、そうでなければ 0 を返します。                                                                                |
| `Delete(S: str*, Index: int, Count: int)`                                                          | `void` | S の Index から Count 文字を削除します（S を参照で変更）。                                                                       |
| `DeleteFile(Filename: str): int`                                                                   | `int`  | コンパイル時にファイルを削除します。成功時は非ゼロを返します。                                                                              |
| `DeleteFileNow(Filename: str): int`                                                                | `int`  | 前処理中に直ちにファイルを削除します。成功時は非ゼロを返します。                                                                             |
| `DimOf(Array): int`                                                                                | `int`  | 配列変数の要素数を返します。                                                                                               |
| `DirExists(Path: str): int`                                                                        | `int`  | ディレクトリが存在すれば 1、そうでなければ 0 を返します。                                                                              |
| `EmitLanguagesSection()`                                                                           | `void` | 同梱の言語ファイルから [Languages] セクションを出力します。                                                                         |
| `EncodeVer(Major: int, Minor: int, Revision: int = 0, Build: int = 0): int`                        | `int`  | バージョン構成要素を 1 つのパックされたバージョン番号にエンコードします。                                                                       |
| `EntryCount(Section: str): int`                                                                    | `int`  | 指定したスクリプトセクション内のエントリ数を返します。                                                                                  |
| `Error(Message: str)`                                                                              | `void` | 指定したメッセージでコンパイル時エラーを発生させます。                                                                                  |
| `Exec(CmdLine: str, Params: str = "", WorkingDir: str = "", ShowCmd: int = 0, Wait: int = 0): int` | `int`  | コンパイル時にプログラムを実行し、プロセスの終了コードを返します。                                                                            |
| `ExecAndGetFirstLine(CmdLine: str, Params: str = "", WorkingDir: str = ""): str`                   | `str`  | プログラムを実行し、標準出力の最初の行を返します。                                                                                    |
| `ExtractFileDir(Filename: str): str`                                                               | `str`  | Filename のディレクトリ部分を返します（末尾のバックスラッシュなし）。                                                                      |
| `ExtractFileExt(Filename: str): str`                                                               | `str`  | Filename の拡張子を返します（先頭のドットを含む）。                                                                               |
| `ExtractFileName(Filename: str): str`                                                              | `str`  | Filename の名前と拡張子の部分を返します。                                                                                    |
| `ExtractFilePath(Filename: str): str`                                                              | `str`  | Filename のドライブとディレクトリの部分を返します（末尾のバックスラッシュ付き）。                                                                |
| `FileClose(Handle: int)`                                                                           | `void` | FileOpen で開いたファイルを閉じます。                                                                                      |
| `FileEof(Handle: int): int`                                                                        | `int`  | 開いているファイルの末尾に達すると非ゼロを返します。                                                                                   |
| `FileExists(Filename: str): int`                                                                   | `int`  | ファイルが存在すれば 1、そうでなければ 0 を返します。                                                                                |
| `FileOpen(Filename: str): int`                                                                     | `int`  | テキストファイルを読み取り用に開き、ファイルハンドルを返します。                                                                             |
| `FileRead(Handle: int): str`                                                                       | `str`  | 開いているファイルから次の行を読み取ります。                                                                                       |
| `FileReset(Handle: int)`                                                                           | `void` | 開いているファイルの読み取り位置を先頭に戻します。                                                                                    |
| `FileSize(Filename: str): int`                                                                     | `int`  | ファイルのサイズをバイト単位で返します。                                                                                         |
| `Find(S: str, Substr: str, Index: int = 1): int`                                                   | `int`  | Index から始めて S 内の Substr の位置を返します。なければ 0。                                                                     |
| `FindClose(Handle: int)`                                                                           | `void` | FindFirst で開いた検索ハンドルを閉じます。                                                                                   |
| `FindCode(): int`                                                                                  | `int`  | [Code] セクションが始まる行インデックスを返します。                                                                                |
| `FindFirst(Pattern: str, Attributes: int = 0): int`                                                | `int`  | ファイル検索を開始してハンドルを返します。見つからなければ負の値。                                                                            |
| `FindGetFileName(Handle: int): str`                                                                | `str`  | 現在の FindFirst/FindNext で見つかったファイル名を返します。                                                                     |
| `FindNext(Handle: int): int`                                                                       | `int`  | ファイル検索を次の一致へ進めます。成功時は非ゼロを返します。                                                                               |
| `FindSection(Section: str): int`                                                                   | `int`  | 指定したセクションヘッダーの行インデックスを返します。                                                                                  |
| `FindSectionEnd(Section: str): int`                                                                | `int`  | 指定したセクションの最後のエントリの次の行インデックスを返します。                                                                            |
| `ForceDirectories(Dir: str): int`                                                                  | `int`  | コンパイル時にディレクトリツリーを作成します。成功時は非ゼロを返します。                                                                         |
| `GetDateTimeString(Format: str, DateSep: str, TimeSep: str): str`                                  | `str`  | 現在の日時を Format に従って整形して返します。                                                                                  |
| `GetEnv(Name: str): str`                                                                           | `str`  | 環境変数の値を返します。                                                                                                 |
| `GetFileCompanyString(Filename: str): str`                                                         | `str`  | ファイルのバージョン情報から CompanyName 文字列を返します。                                                                         |
| `GetFileCopyrightString(Filename: str): str`                                                       | `str`  | ファイルのバージョン情報から LegalCopyright 文字列を返します。                                                                      |
| `GetFileDateTimeString(Filename: str, Format: str, DateSep: str, TimeSep: str): str`               | `str`  | ファイルの最終更新日時を Format に従って整形して返します。                                                                            |
| `GetFileDescriptionString(Filename: str): str`                                                     | `str`  | ファイルのバージョン情報から FileDescription 文字列を返します。                                                                     |
| `GetFileOriginalFilenameString(Filename: str): str`                                                | `str`  | ファイルのバージョン情報から OriginalFilename 文字列を返します。                                                                    |
| `GetFileProductVersionString(Filename: str): str`                                                  | `str`  | ファイルのバージョン情報から ProductVersion 文字列を返します。                                                                      |
| `GetFileVersionString(Filename: str): str`                                                         | `str`  | 実行ファイルや DLL のファイルバージョンをドット区切り文字列で返します（例 <code>1.2.3.4</code>）。                                               |
| `GetMD5OfFile(Filename: str): str`                                                                 | `str`  | ファイルの MD5 ハッシュを 16 進文字列で返します。                                                                                |
| `GetMD5OfString(S: str): str`                                                                      | `str`  | ANSI 文字列の MD5 ハッシュを 16 進文字列で返します。                                                                            |
| `GetMD5OfUnicodeString(S: str): str`                                                               | `str`  | Unicode 文字列の MD5 ハッシュを 16 進文字列で返します。                                                                         |
| `GetPackedVersion(Filename: str): int`                                                             | `int`  | ファイルのパック（エンコード済み）バージョン番号を返します。                                                                               |
| `GetSHA1OfFile(Filename: str): str`                                                                | `str`  | ファイルの SHA-1 ハッシュを 16 進文字列で返します。                                                                              |
| `GetSHA1OfString(S: str): str`                                                                     | `str`  | ANSI 文字列の SHA-1 ハッシュを 16 進文字列で返します。                                                                          |
| `GetSHA1OfUnicodeString(S: str): str`                                                              | `str`  | Unicode 文字列の SHA-1 ハッシュを 16 進文字列で返します。                                                                       |
| `GetSHA256OfFile(Filename: str): str`                                                              | `str`  | ファイルの SHA-256 ハッシュを 16 進文字列で返します。                                                                            |
| `GetSHA256OfString(S: str): str`                                                                   | `str`  | ANSI 文字列の SHA-256 ハッシュを 16 進文字列で返します。                                                                        |
| `GetSHA256OfUnicodeString(S: str): str`                                                            | `str`  | Unicode 文字列の SHA-256 ハッシュを 16 進文字列で返します。                                                                     |
| `GetStringFileInfo(Filename: str, Key: str): str`                                                  | `str`  | ファイルのバージョン情報から文字列を返します。一般的なキー：<code>FileVersion</code>、<code>ProductVersion</code>、<code>CompanyName</code>。 |
| `GetVersionComponents(Filename: str, Major: int*, Minor: int*, Revision: int*, Build: int*): int`  | `int`  | ファイルのバージョン構成要素を参照変数に読み込みます。成功時は非ゼロを返します。                                                                     |
| `GetVersionNumbers(Filename: str, VersionMS: int*, VersionLS: int*): int`                          | `int`  | ファイルのバージョンを参照する上位/下位ワードに読み込みます。成功時は非ゼロを返します。                                                                 |
| `GetVersionNumbersString(Filename: str): str`                                                      | `str`  | ファイルのバージョンをドット区切り文字列で返します（例 <code>1.2.3.4</code>）。                                                           |
| `Insert(Source: str, S: str*, Index: int)`                                                         | `void` | Source を S の Index に挿入します（S を参照で変更）。                                                                         |
| `Int(Value: any, Default: int = 0): int`                                                           | `int`  | 値を整数に変換します。変換に失敗した場合は Default を使用します。                                                                        |
| `Is64BitPEImage(Filename: str): int`                                                               | `int`  | 指定した PE イメージが 64 ビットなら非ゼロを返します。                                                                              |
| `IsWin64(): int`                                                                                   | `int`  | コンパイラーが 64 ビット Windows 上で動作している場合に非ゼロを返します。                                                                  |
| `Len(S: str): int`                                                                                 | `int`  | 文字列の長さを返します。                                                                                                 |
| `LowerCase(S: str): str`                                                                           | `str`  | 文字列を小文字に変換して返します。                                                                                            |
| `Max(A: int, B: int): int`                                                                         | `int`  | 2 つの整数のうち大きい方を返します。                                                                                          |
| `Message(S: str)`                                                                                  | `void` | コンパイラーログに情報メッセージを出力します。                                                                                      |
| `Min(A: int, B: int): int`                                                                         | `int`  | 2 つの整数のうち小さい方を返します。                                                                                          |
| `PackVersionComponents(Major: int, Minor: int, Revision: int, Build: int): int`                    | `int`  | バージョン構成要素を 1 つのパックされたバージョン番号にまとめます。                                                                          |
| `PackVersionNumbers(VersionMS: int, VersionLS: int): int`                                          | `int`  | 上位/下位バージョンワードを 1 つのパックされたバージョン番号にまとめます。                                                                      |
| `Pos(Substr: str, S: str): int`                                                                    | `int`  | S 内の Substr の 1 始まりの位置を返します。なければ 0。                                                                          |
| `Power(Base: int, Exponent: int): int`                                                             | `int`  | Base の Exponent 乗を返します。                                                                                      |
| `ReadIni(Filename: str, Section: str, Key: str, Default: str = ""): str`                           | `str`  | コンパイル時に INI ファイルから値を読み取ります。                                                                                  |
| `ReadReg(RootKey: int, SubKeyName: str, ValueName: str = "", Default: str = ""): str`              | `str`  | コンパイル時にレジストリ値を読み取ります。                                                                                        |
| `RemoveBackslashUnlessRoot(S: str): str`                                                           | `str`  | S がドライブのルートでない限り、末尾のバックスラッシュを削除します。                                                                          |
| `RemoveFileExt(Filename: str): str`                                                                | `str`  | Filename から拡張子を除いて返します。                                                                                      |
| `RPos(Substr: str, S: str): int`                                                                   | `int`  | S 内の Substr の最後の出現位置を 1 始まりで返します。なければ 0。                                                                     |
| `SamePackedVersion(Version1: int, Version2: int): int`                                             | `int`  | 2 つのパックされたバージョン番号が等しければ非ゼロを返します。                                                                             |
| `SameStr(S1: str, S2: str): int`                                                                   | `int`  | 2 つの文字列が等しければ（大文字小文字を無視）非ゼロを返します。                                                                            |
| `SameText(S1: str, S2: str): int`                                                                  | `int`  | 2 つの文字列が等しければ（大文字小文字を無視）非ゼロを返します。                                                                            |
| `SaveStringToFile(Filename: str, S: str, Append: int = 0)`                                         | `void` | 文字列をファイルに書き込みます。任意で追記します。                                                                                    |
| `SaveToFile(Filename: str)`                                                                        | `void` | これまでに収集した前処理出力をファイルに書き込みます（デバッグ用）。                                                                           |
| `SetSetupSetting(Name: str, Value: str)`                                                           | `void` | コンパイル時に [Setup] セクションのディレクティブを設定します。                                                                         |
| `SetupSetting(Name: str): str`                                                                     | `str`  | 名前で [Setup] セクションのディレクティブの値を返します。                                                                            |
| `Str(Value: any): str`                                                                             | `str`  | 値を文字列に変換します。整数はテキストに、void は空文字列になります。                                                                        |
| `StringChange(S: str*, FromStr: str, ToStr: str): int`                                             | `int`  | S 内の FromStr をすべて ToStr に置換します。置換回数を返します。                                                                    |
| `StrToVersion(S: str): int`                                                                        | `int`  | ドット区切りのバージョン文字列をパックされたバージョン番号に解析します。                                                                         |
| `Trim(S: str): str`                                                                                | `str`  | S の先頭と末尾の空白を除いて返します。                                                                                         |
| `TypeOf(Ident): int`                                                                               | `int`  | 識別子の型を返します：0=void、1=int、2=str。                                                                               |
| `UnpackVersionComponents(Version: int, Major: int*, Minor: int*, Revision: int*, Build: int*)`     | `void` | パックされたバージョン番号を参照構成要素に分割します。                                                                                  |
| `UnpackVersionNumbers(Version: int, VersionMS: int*, VersionLS: int*)`                             | `void` | パックされたバージョン番号を参照する上位/下位ワードに分割します。                                                                            |
| `UpperCase(S: str): str`                                                                           | `str`  | 文字列を大文字に変換して返します。                                                                                            |
| `VersionToStr(Version: int): str`                                                                  | `str`  | パックされたバージョン番号をドット区切りのバージョン文字列に変換します。                                                                         |
| `Warning(Message: str)`                                                                            | `void` | 指定したメッセージでコンパイル時警告を発行します。                                                                                    |
| `WriteIni(Filename: str, Section: str, Key: str, Value: str)`                                      | `void` | コンパイル時に INI ファイルへ値を書き込みます。                                                                                   |
| `YesNo(S: str): int`                                                                               | `int`  | 文字列が肯定（yes/true）を表す場合に非ゼロを返します。                                                                              |

---

## 定義済み変数

独自の `#define` に加えて、ISPP は宣言なしで使用できる**定義済み変数**のセットを提供します。**値を持つ**もの（`int` / `str`
）は、ユーザー定義と同様に `{#…}` でインライン出力でき、式でも使用できます。**値を持たない**もの（`void`
）は条件付きコンパイル専用です。以下のリストは完全です：

| 変数                 | 型      | 説明                                       |
|--------------------|--------|------------------------------------------|
| `__COUNTER__`      | `int`  | 自動増加カウンター。使用するたびに増加します。                  |
| `__LINE__`         | `int`  | 現在のファイル内の現在の行番号。                         |
| `__FILENAME__`     | `str`  | 現在のインクルードファイルパスのファイル名部分。                 |
| `__PATHFILENAME__` | `str`  | 現在のインクルードファイルのフルパス。                      |
| `__DIR__`          | `str`  | 現在のインクルードファイルパスのディレクトリ部分。                |
| `__INCLUDE__`      | `str`  | 現在のインクルードパス（複数のパスはセミコロンで区切られる）。          |
| `__WIN32__`        | `void` | 常に定義済み。#ifdef で ISPP 環境の検出に使用できます。       |
| `ISPP_INVOKED`     | `void` | ISPP が有効なときは常に定義済み。                      |
| `ISCC_INVOKED`     | `void` | コンソールモードコンパイラー（ISCC.exe）でコンパイルする場合に定義済み。 |
| `PREPROCVER`       | `int`  | Inno Setup プリプロセッサーの 32 ビットパックバージョン番号。   |
| `Ver`              | `int`  | PREPROCVER の別名。                          |
| `WINDOWS`          | `void` | 常に定義済み。                                  |
| `UNICODE`          | `void` | 常に定義済み（ISPP は Unicode 専用）。               |
| `CompilerPath`     | `str`  | Inno Setup コンパイラー（ISCC.exe）があるディレクトリ。    |
| `SourcePath`       | `str`  | ルートスクリプトファイルを含むディレクトリ。                   |
| `SysPath`          | `str`  | コンパイラーの種類に応じたシステムディレクトリ。                 |
| `NewLine`          | `str`  | 改行文字シーケンス。                               |
| `Tab`              | `str`  | タブ文字。                                    |

これらは `{#…}` 補完に表示され、検証で受け入れられます。パスに関連するもの（`{#SourcePath}`、`{#__DIR__}`、`{#CompilerPath}`、
`{#SysPath}`）は、プラグインが `[Languages]` の `MessagesFile` パスを解決する際にも展開されます。残りの動的なものは、誤ったエラーを生成せずに未解決のままになります。

!!! note "値を持たないシンボル"
`__WIN32__`、`ISPP_INVOKED`、`ISCC_INVOKED`、`WINDOWS`、`UNICODE` は**値を持ちません**。これらは条件付きコンパイル（`#ifdef` /
`#if defined(...)`）のために*定義される*だけで、`{#…}` で出力**できません**。`{#…}` 補完から除外され、インライン出力として受け入れられません。
