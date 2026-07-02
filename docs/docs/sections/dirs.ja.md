# [Dirs]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=dirssection){
.md-button .md-button--primary }

`[Dirs]` セクションはインストール中にターゲットマシンに追加のディレクトリを作成します。ほとんどの場合、このセクションはまったく必要ありません——
`[Files]` に列挙されたファイルに必要なディレクトリは自動的に作成されます。空のディレクトリ構造を作成する必要がある場合、特定の
NTFS 属性を設定する場合、またはディレクトリの ACL 権限を設定する場合に `[Dirs]` を使用します。

---

## Name

`string` · **必須**

作成するディレクトリの完全なパス（例：`{app}\data`）。すべての Inno Setup ディレクトリ定数をサポートします。

---

## Attribs

`string` · **複数の値**

ディレクトリに設定するファイルシステム属性：`readonly`、`hidden`、`system`、`notcontentindexed`。

---

## Permissions

`string` · **複数の値**

ディレクトリに付与する ACL 権限（例：`users-modify`、`everyone-readexec`）。`{sys}` や `{commonpf}` などのトップレベルシステムディレクトリへの
ACL 設定は避けてください。

---

## Flags

`string` · **複数の値**

動作フラグ：`deleteafterinstall`、`setntfscompression`、`uninsalwaysuninstall`、`uninsneveruninstall`、
`unsetntfscompression`。

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

`[Code]` の `Boolean` を返す Pascal 関数名。関数が `True` を返す場合にのみ処理されます。

---

## BeforeInstall

`string`

このディレクトリが作成される直前に呼び出される `[Code]` の Pascal プロシージャ名。

---

## AfterInstall

`string`

このディレクトリが作成された直後に呼び出される `[Code]` の Pascal プロシージャ名。

---

## MinVersion

`string`

このエントリーが適用される最低 Windows バージョン。`0` を使用すると適用しません。

---

## OnlyBelowVersion

`string`

このエントリーが適用される最高 Windows バージョン（排他的）。`0` を使用すると上限なし。
