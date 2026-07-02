# [Languages]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=languagessection){
.md-button .md-button--primary }

`[Languages]` セクションはインストーラーで使用可能なすべての言語を宣言します。各エントリーはウィザード UI の翻訳済み文字列を提供する
ISL メッセージファイルを指します。リストの最初のエントリーがデフォルト言語になります。ここで定義された言語は、他のセクションの
`Languages` 共通パラメーターを通じて参照でき、エントリーを特定のロケールに制限します。

---

## Name

`string` · **必須**

内部言語識別子（例：`english`、`german`）。他のセクションの `Languages` パラメーターから参照されます。

---

## MessagesFile

`string` · **必須**

ISL メッセージファイルへのパス。組み込みの英語メッセージには `compiler:Default.isl`、バンドルされた翻訳の 1 つには
`compiler:Languages\German.isl` を使用します。
