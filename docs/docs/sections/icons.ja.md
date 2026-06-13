# [Icons]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=iconssection){ .md-button .md-button--primary }

`[Icons]` セクションはインストール中に Windows ショートカットを作成します——スタートメニュー、デスクトップ、またはその他の場所。各エントリーは正確に 1 つのショートカットを作成します。ターゲットは実行可能ファイル、ドキュメント、フォルダー、または URL にできます。`Tasks` パラメーターを使用してショートカットをオプションにし、ユーザーが*追加タスクの選択*ウィザードページで決定できるようにします。

---

## Name

`string` · **必須**

ショートカットの完全なパスと名前（例：`{group}\My Program` または `{commondesktop}\My Program`）。

---

## Filename

`string` · **必須**

ショートカットのターゲット——実行可能ファイル、ドキュメント、フォルダー、または URL。

---

## Parameters

`string`

ショートカットがアクティブ化されたときにターゲットに渡すコマンドライン引数。

---

## WorkingDir

`string`

ショートカットが起動されるときに設定される作業ディレクトリ。デフォルトはターゲットを含むディレクトリ。

---

## HotKey

`string`

ターゲットを起動するグローバルキーボードショートカット（例：`ctrl+alt+k`）。

---

## Comment

`string`

ユーザーがショートカット上にカーソルを置いたときに表示されるツールチップテキスト。

---

## IconFilename

`string`

このショートカットのアイコンを含む `.ico`、`.exe`、または `.dll` へのパス。

---

## IconIndex

`integer`

`IconFilename` 内のアイコンのゼロベースのインデックス。デフォルトは `0`。

---

## AppUserModelID

`string`

Windows 7+ のアプリケーションユーザーモデル ID。タスクバーボタンのグループ化とトースト通知の関連付けに使用されます。

---

## AppUserModelToastActivatorCLSID

`string` · **6.1 以降**

このショートカットを通じたトースト通知のアクティブ化用の Windows 10+ COM CLSID。

---

## Flags

`string` · **複数の値**

動作フラグ：`runminimized`、`runmaximized`、`closeonexit`、`createonlyiffileexists`、`preventpinning`。

---

## Components

`→ Components` · **複数の値**

リストされたコンポーネントの少なくとも 1 つが選択されている場合にのみ処理されます。

---

## Tasks

`→ Tasks` · **複数の値**

リストされたタスクの少なくとも 1 つがチェックされている場合にのみ処理されます。

---

## Languages

`→ Languages` · **複数の値**

このエントリーを指定された言語に限定します。

---

## Check

`string`

`[Code]` の `Boolean` を返す Pascal 関数名。関数が `True` を返す場合にのみ処理されます。

---

## BeforeInstall

`string`

このショートカットが作成される直前に呼び出される `[Code]` の Pascal プロシージャ名。

---

## AfterInstall

`string`

このショートカットが作成された直後に呼び出される `[Code]` の Pascal プロシージャ名。

---

## MinVersion

`string`

このエントリーが適用される最低 Windows バージョン。`0` を使用すると適用しません。

---

## OnlyBelowVersion

`string`

このエントリーが適用される最高 Windows バージョン（排他的）。`0` を使用すると上限なし。
