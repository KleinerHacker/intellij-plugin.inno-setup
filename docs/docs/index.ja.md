# Inno Setup – JetBrains プラグイン

**すべての JetBrains IDE で Inno Setup スクリプト（`.iss`）と言語ファイル（`.isl`）をファーストクラスでサポート。**

---

## Inno Setup とは？

[Inno Setup](https://jrsoftware.org/isinfo.php) は Jordan Russell と Martijn Laan が作成した、無料でオープンソースの
Windows インストーラービルダーです。1997 年に初めてリリースされ、Windows エコシステムで最も広く使われているインストーラーツールの一つとなっています。
**Visual Studio Code**、**Git for Windows**、**Embarcadero Delphi** などのプロジェクトのインストーラーを支えています。

Inno Setup スクリプト（`.iss`）は完全なインストーラー設定を記述します：インストールするファイル、作成するレジストリキー、追加するショートカット、インストールウィザードの動作方法。言語ファイル（
`.isl`）はローカライズされたウィザードテキストと言語メタデータを提供します。合わせて豊富なセクション、パラメーター、メッセージ、そして完全なランタイムカスタマイズのための統合
Pascal スクリプトエンジンをサポートします。

!!! tip "公式 Inno Setup リソース"

- :octicons-home-16: [ホームページ](https://jrsoftware.org/isinfo.php)
- :octicons-book-16: [ドキュメント](https://jrsoftware.org/ishelp/)
- :octicons-download-16: [ダウンロード](https://jrsoftware.org/isdl.php)

---

## このプラグインの機能

このプラグインは **IntelliJ IDEA**、**PyCharm**、**CLion**、**Rider**、**WebStorm**、**GoLand** などすべての JetBrains IDE で
`.iss` および `.isl` ファイルのファーストクラスエディターサポートを提供します：

- **シンタックスハイライト** — セクション、ディレクティブ、パラメーター、値、定数、Pascal コードブロックを色分け表示
- **コード補完** — セクションヘッダー、パラメーター名、既知の値を入力時に候補表示
- **インラインドキュメント** — ディレクティブやパラメーターにカーソルを当てると IDE を離れずに説明を表示
- **参照解決** — スクリプト内のコンポーネント、タスク、タイプ定義間をナビゲート
- **ローカリゼーションサポート** — `[Messages]`、`[CustomMessages]`、`[LangOptions]`、言語プレフィックス、`{cm:...}`
  参照を完全サポート
- **言語メタデータ** — Windows LCID 補完、組み込み Inno Setup 言語候補、言語参照のフラグインレイ
- **構造ビュー** — プロジェクトツールウィンドウですべてのセクションとエントリを俯瞰
- **定数サポート** — `{app}`、`{autopf}`、`{group}` などすべての組み込み定数を引用符内も含めて認識・検証

---

## IDE 互換性

このプラグインはすべての完全な JetBrains IDE に存在する言語サポートモジュール `com.intellij.modules.lang`
に対してビルドされています。IntelliJ IDEA を特に必要とせず、独自の YAML 解析インフラを持つためホスト IDE
への隠れた実行時依存関係がありません。

| IDE                                 | サポート |
|-------------------------------------|------|
| IntelliJ IDEA（Community & Ultimate） | ✔    |
| PyCharm（Community & Professional）   | ✔    |
| CLion / CLion Nova                  | ✔    |
| Rider                               | ✔    |
| WebStorm                            | ✔    |
| GoLand                              | ✔    |
| RubyMine                            | ✔    |
| DataGrip                            | ✔    |
| その他の IntelliJ プラットフォーム IDE          | ✔    |

---

## インストール

このプラグインは**まだ JetBrains Marketplace で公開されていません**。ローカルでビルドした JAR/ZIP から手動でインストールしてください：

### 1 · プラグインをビルド

```bash
./gradlew buildPlugin
```

配布可能な ZIP は `build/distributions/` に書き出されます。

### 2 · IDE にインストール

1. **設定 / 環境設定 → プラグイン** を開く
2. ⚙ 歯車アイコンをクリックして **ディスクからプラグインをインストール…** を選択
3. `build/distributions/` から ZIP ファイルを選択
4. プロンプトが表示されたら IDE を再起動

!!! note "Marketplace への公開"
JetBrains Marketplace へのリリースが予定されています。公開後は IDE 組み込みのプラグインブラウザから直接インストールできます。

---

## セクションリファレンス

上部のナビゲーションには Inno Setup スクリプトのセクションリファレンスと、型情報および公式 Inno Setup ドキュメントへのリンクを含む専用の
`.isl` 言語ファイルリファレンスが含まれています。
