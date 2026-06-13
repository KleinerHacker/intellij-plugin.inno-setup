# [UninstallRun]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=uninstallrunsection){
.md-button .md-button--primary }

`[UninstallRun]` セクションは `[Run]` と全く同じように機能しますが、エントリーはインストール後ではなくアンインストールの*開始*時に実行されます。サービスの停止、実行中のプロセスの終了、またはファイルとレジストリキーの削除だけでは処理できない状態のクリーンアップに使用します。エントリーは表示順に実行されます。

---

## Filename

`string` · **必須**

アンインストール中に起動する実行可能ファイル、ドキュメント、またはフォルダーへのパス。

---

## Description

`string`

アンインストール後ページに表示されるオプションのチェックボックスのラベル。`postinstall` フラグが必要です。

---

## Parameters

`string`

`Filename` に渡すコマンドライン引数。

---

## WorkingDir

`string`

起動するプロセスの作業ディレクトリ。デフォルトは `Filename` を含むディレクトリ。

---

## StatusMsg

`string`

このエントリーが実行中に進行状況ウィンドウに表示されるステータスメッセージ。

---

## RunOnceId

`string`

このエントリーが複数のアンインストール実行で複数回実行されるのを防ぐ一意の識別子。

---

## Verb

`string`

`shellexec` フラグと共に使用するシェル動詞（例：`open`、`print`）。

---

## OnLog

`string`

各出力行に対して呼び出される `[Code]` の Pascal プロシージャ名（`logoutput` フラグが必要）。

---

## Flags

`string` · **複数の値**

動作フラグ：`postinstall`、`shellexec`、`nowait`、`runhidden`、`skipifsilent`、`skipifnotsilent`、`unchecked`、`waituntilterminated`、`waituntilidle`、`logoutput`、`runasoriginaluser`。

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

このエントリーが処理される直前に呼び出される `[Code]` の Pascal プロシージャ名。

---

## AfterInstall

`string`

このエントリーが処理された直後に呼び出される `[Code]` の Pascal プロシージャ名。

---

## MinVersion

`string`

このエントリーが適用される最低 Windows バージョン。`0` を使用すると適用しません。

---

## OnlyBelowVersion

`string`

このエントリーが適用される最高 Windows バージョン（排他的）。`0` を使用すると上限なし。
