# [CustomMessages]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=custommessagessection){
.md-button .md-button--primary }

`[CustomMessages]` セクションはプロジェクト固有のローカライズ可能な文字列を定義します。これらの文字列は `{cm:MessageName}` 定数を通じて他のセクションや Pascal コードから参照できます。

```ini
[CustomMessages]
WelcomeText=Welcome to My App
german.WelcomeText=Willkommen bei My App
```

---

## メッセージ名

`string`

このセクションには事前定義されたキーがありません。メッセージ名はスクリプト作者が選択し、オプションで言語名をプレフィックスとして付けることができます（例：`german.WelcomeText`）。

---

## 参照

`{cm:MessageName}` は一致する `[CustomMessages]` エントリーに解決されます。プラグインは `{cm:` の後の補完、使用箇所の検索、リネームリファクタリング、未解決参照のハイライト表示をサポートします。

メッセージがリネームされると、プラグインは言語バリアントと `{cm:...}` の使用箇所を同期して維持します。
