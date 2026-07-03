# [Messages]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=messagessection){
.md-button .md-button--primary }

`[Messages]` セクションは `Default.isl` または選択された言語ファイルからの組み込みインストーラー UI
文字列をオーバーライドします。各エントリーはディレクティブ構文を使用します：

```ini
MessageID=Text
german.MessageID=Text
```

オプションの言語プレフィックスは `[Languages]` で宣言された単一の言語をターゲットにします。

---

## 既知のメッセージ ID

プラグインは標準の `Default.isl` メッセージ識別子を既知のキーとして内蔵しています。補完はメッセージ ID
を提案し、ドットの前で使用した場合は利用可能な言語プレフィックスを提案します。

---

## 言語プレフィックス

言語プレフィックスは `[Languages]`
エントリーに解決されます。ターゲット言語が解決できる場合、プラグインはこれらのプレフィックスの補完、ナビゲーション、使用箇所の検索、リネーム、言語フラグのインレイをサポートします。

---

## 値

`string`

Setup または Uninstall が表示するメッセージテキスト。ランタイム置換を期待するメッセージをオーバーライドする際は `%1` や
`%2` などのプレースホルダーを保持してください。
