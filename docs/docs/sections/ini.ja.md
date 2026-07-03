# [INI]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=inissection){
.md-button .md-button--primary }

!!! warning "プラグインサポート"
`[INI]` は現在のプラグイン仕様では未実装としてマークされています。このセクションはドキュメントカバレッジのためにここに記載されていますが、仕様が実装されるまで補完と検証のサポートが不完全な場合があります。

`[INI]` セクションはインストール中にユーザーのシステムの `.ini` ファイルのエントリーを作成または更新します。設定をレジストリやアプリケーションデータファイルではなく
INI ファイルに保存するレガシーアプリケーションに便利です。

---

## Filename

`string` · **必須**

変更する `.ini` ファイルへのパス（例：`{app}\MyApp.ini` または `{win}\MyApp.ini`）。

---

## Section

`string` · **必須**

キーを含む INI セクション名。

---

## Key

`string`

作成、更新、または削除する INI キーの名前。

---

## String

`string`

キーに書き込む値。

---

## Flags

`string` · **複数の値**

動作フラグ：`createkeyifdoesntexist`、`uninsdeleteentry`、`uninsdeletesection`、`uninsdeletesectionifempty`。

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

## MinVersion

`string`

このエントリーが適用される最低 Windows バージョン。`0` を使用すると適用しません。

---

## OnlyBelowVersion

`string`

このエントリーが適用される最高 Windows バージョン（排他的）。`0` を使用すると上限なし。
