# [Components]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=componentssection){
.md-button .md-button--primary }

`[Components]` セクションはウィザードのコンポーネント選択ページに表示される選択可能な機能を定義します。コンポーネントはバックスラッシュ表記を使用して親/子階層に整理できます（例：
`extra\plugins`）。`[Files]`、`[Icons]` などのセクションのエントリーは `Components`
パラメーターを通じてコンポーネントにリンクされるため、選択されたコンポーネントに属するファイルのみがインストールされます。

---

## Name

`string` · **必須**

このコンポーネントの内部識別子。階層にはバックスラッシュ表記を使用（例：`main\help`）。

---

## Description

`string` · **必須**

ウィザードのコンポーネント選択リストに表示されるこのコンポーネントのラベル。

---

## Types

`→ Types` · **複数の値**

デフォルトでこのコンポーネントを含むインストールタイプ名（`[Types]` から）のスペース区切りリスト。

---

## ExtraDiskSpaceRequired

`integer`

このコンポーネントがインストールするファイル以外に必要な追加ディスク容量（バイト単位）。コンポーネント選択ページに表示されます。

---

## Flags

`string` · **複数の値**

動作フラグ：`fixed`、`checkablealone`、`exclusive`、`restart`、`dontinheritcheck`、`disablenouninstallwarning`。

---

## Check

`string`

`[Code]` の `Boolean` を返す Pascal 関数名。関数が `True` を返す場合にのみ、このエントリーが処理されます。

---

## Languages

`→ Languages` · **複数の値**

このエントリーを指定された言語に限定します。
