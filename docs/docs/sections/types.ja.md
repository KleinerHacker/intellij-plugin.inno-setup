# [Types]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=typessection){
.md-button .md-button--primary }

`[Types]` セクションは、ウィザードのコンポーネント選択ページに表示される名前付きインストールプロファイルを定義します——例えば
*フル*、*コンパクト*、*カスタム*。`[Components]` の各コンポーネントは 1 つ以上のタイプを参照して、デフォルトでどのプロファイルにそれが含まれるかを宣言します。
`iscustom` フラグを使用してユーザーカスタマイズ可能なタイプとしてマークできるタイプは 1 つのみです。

---

## Name

`string` · **必須**

このインストールタイプの内部識別子。`[Components]` の `Types` パラメーターから参照されます。

---

## Description

`string` · **必須**

ウィザードでこのインストールタイプに表示される人間が読めるラベル。

---

## Flags

`string` · **複数の値**

動作フラグ。`iscustom` はこのタイプをユーザーカスタマイズ可能なタイプとしてマークします——スクリプトごとに 1
つのタイプのみこのフラグを持つことができます。
