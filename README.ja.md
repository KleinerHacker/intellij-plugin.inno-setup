<p align="center">
  <img src="docs/docs/assets/images/inno-setup-logo.png" alt="Inno Setup Logo" width="256"/>
</p>

<p align="center">
  <a href="README.md">English</a> ·
  <a href="README.zh.md">简体中文</a> ·
  <b>日本語</b> ·
  <a href="README.ko.md">한국어</a>
</p>

# Inno Setup – JetBrains プラグイン

IntelliJ プラットフォームファミリー全体に [Inno Setup](https://jrsoftware.org/isinfo.php)
スクリプト（`.iss`）向けの本格的な言語サポートをもたらす JetBrains IDE プラグインです。

---

## 概要

[Inno Setup](https://jrsoftware.org/isinfo.php) は Jordan Russell と Martijn Laan によって開発された、
広く使われている無料の Windows インストーラービルダーです（1997 年に初リリース）。そのスクリプト（`.iss`）は
インストーラー全体——ファイル、レジストリキー、ショートカット、そしてオプションの Pascal スクリプト——を
記述しますが、これまで JetBrains IDE には専用のエディターサポートがありませんでした。

本プラグインはそのギャップを埋めます。目標は、どの JetBrains IDE を使っていても、正しいハイライト、
コンテキストに応じた補完、インラインドキュメント、検証済みの参照といった、`.iss` ファイルの完全な編集体験を
提供することです。

### 機能

| 機能                     | 説明                                                                                                              |
|--------------------------|-------------------------------------------------------------------------------------------------------------------|
| **構文ハイライト**       | セクション、ディレクティブ、パラメーター、定数（`{app}`、`{autopf}`……）、Pascal コードブロックが色分けされます    |
| **コード補完**           | 入力に応じてセクション名、ディレクティブキー、パラメーターキー、既知のフラグ値が提案されます                       |
| **インラインドキュメント** | 任意のディレクティブやパラメーターにカーソルを合わせると、IDE を離れずに説明を読めます                            |
| **参照解決**             | `Name:` 宣言と、`Tasks:`・`Components:`・`Types:` パラメーター内でのその使用箇所との間を移動できます               |
| **構造ビュー**           | すべてのセクションとそのエントリを俯瞰できます                                                                     |
| **定数検証**             | 引用符付き文字列に埋め込まれたものも含め、組み込み定数が認識・検証されます                                         |
| **括弧/引用符のマッチング** | `{`、`[`、`"` を自動的に閉じます                                                                                  |
| **コード折りたたみ**     | セクション、長いパラメーターエントリ、`#if … #endif` ブロックを個別に折りたためます                               |
| **コード整形**           | コードの整形で `=` / `:` / `;` と `[ ]` の周囲のスペース、セクション間の 1 行の空行、プリプロセッサの算術演算子の空白を統一します（コードスタイルで設定可能） |
| **インラインヒント**     | `Languages:` パラメーター値の横に言語フラグアイコンがインライン表示されます                                        |
| **ビルド統合**           | コンテキストメニュー操作で `.iss` スクリプトを直接コンパイル。プロジェクトビルド時に ISCC を自動実行することも可能 |
| **言語ファイルサポート** | `.isl` 言語ファイルが `.iss` スクリプトと並んで認識・ハイライト・検証されます                                      |
| **ISPP サポート**        | プリプロセッサディレクティブ（スコープキーワード付きの `#define`/`#undef`、`#include`、`#if`/`#elif`/`#else`/`#endif`……）が解析・ハイライト・補完・検証・参照解決されます |

### IDE 互換性

本プラグインは `com.intellij.modules.lang`——すべての完全な IntelliJ プラットフォーム IDE で利用可能——を
ターゲットとし、独自のランタイム依存関係をバンドルしているため、ホスト IDE に対する隠れた要件はありません。

対応 IDE：**IntelliJ IDEA**、**PyCharm**、**CLion / CLion Nova**、**Rider**、**WebStorm**、**GoLand**、
**RubyMine**、**DataGrip**、その他すべての IntelliJ プラットフォーム IDE。

---

## はじめに（開発）

### 前提条件

| ツール        | バージョン                                   |
|---------------|----------------------------------------------|
| JDK           | 21 以降                                      |
| IntelliJ IDEA | 2024.1 以降（IDE 支援による開発の場合）      |
| Gradle        | Gradle Wrapper で提供——インストール不要      |

### ビルド

```bash
# リポジトリをクローン
git clone https://github.com/KleinerHacker/inno-setup.git
cd inno-setup

# パーサー/レキサーを生成し、すべてのモジュールをコンパイル
./gradlew assemble

# すべてのテストを実行（:plugin モジュールにあります）
./gradlew :plugin:test

# 配布可能なプラグイン ZIP をビルド
./gradlew :plugin:buildPlugin
# → plugin/build/distributions/inno-setup-<version>.zip
```

### サンドボックス IDE で実行

```bash
./gradlew runIde
```

これにより、プラグインが読み込まれた新しい IntelliJ IDEA インスタンスが、通常の IDE インストールから隔離された
状態で起動します。任意の `.iss` ファイルを開くか作成して、プラグインをその場で試せます。

### IntelliJ IDEA から実行 / デバッグ

`.run/` に構成済みの実行構成が含まれています：

| 構成                  | 動作                                                       |
|-----------------------|------------------------------------------------------------|
| **Run Plugin**        | `:runIde` を起動——プラグイン入りのサンドボックス IDE を開く |
| **Run Tests**         | `:test` を実行                                             |
| **Run Verifications** | `:verifyPlugin` を実行して互換性を確認                     |

### プロジェクト構成

依存チェーン `:plugin → :language:script → :language:preprocessor` を持つ **Gradle マルチモジュール**
ビルドです。ルートプロジェクトは純粋なアグリゲーター（コードなし、`plugin.xml` なし）です。

```
.
├── language/
│   ├── preprocessor/        ISPP プリプロセッサ言語（レキサー/パーサー/PSI、ハイライター、アノテーター、
│   │                        括弧マッチャー、参照、式エンジン、ISPP 仕様、PluginBundle）
│   │   └── src/main/{kotlin, resources/{META-INF, parsing, spec, messages}}
│   └── script/              Inno Setup 言語：セクション/INI 文法（.iss/.isl/.ist）、ファイルタイプ、
│       │                    ハイライター、折りたたみ、アノテーター、参照、include インフラ、ISPP インジェクター、
│       │                    spec/settings サービス
│       └── src/main/{kotlin, resources/{META-INF, parsing, spec, icons}}
├── plugin/                  公開可能なプラグイン：IDE 機能、ビルド/実行、設定 UI、メイン plugin.xml、
│   │                        カラースキーム、アイコン——およびすべてのテスト
│   └── src/{main, test}/
├── buildSrc/                共有 Gradle 規約（inno-setup.platform-module）
├── <module>/build/generated/  モジュールごとに生成されたパーサー/レキサー/PSI（自動生成）
├── docs/                    MkDocs ドキュメントサイト
├── build.gradle.kts         ルートアグリゲーター（全モジュールの Dokka、kover マージ、MkDocs、generateSources）
└── settings.gradle.kts
```

> **注意：** 生成されたソースはモジュールごとに `<module>/build/generated/` に置かれます。`./gradlew
> generateSources`（ルートの一括タスク）またはモジュールごとの `generateIs*Parser`/`generateIs*Lexer`
> タスクで再生成してください。手動で編集しないでください——ビルドのたびに上書きされます。

---

## 手動インストール

本プラグインは **まだ JetBrains Marketplace では入手できません**。ビルド済みの ZIP から手動でインストールしてください：

### ステップ 1 — プラグイン ZIP をビルド

```bash
./gradlew buildPlugin
```

出力は `build/distributions/inno-setup-<version>.zip` に書き込まれます。

### ステップ 2 — IDE にインストール

1. JetBrains IDE を開き、**Settings / Preferences → Plugins** に移動します
2. Plugins パネル右上の **⚙ 歯車アイコン** をクリックします
3. **Install Plugin from Disk…** を選択します
4. `build/distributions/` に移動して `.zip` ファイルを選択します
5. **OK** をクリックし、プロンプトが表示されたら **IDE を再起動** します

再起動後、`.iss` 拡張子を持つファイルはすべて自動的にプラグインで処理されます。

---

## ドキュメント

[完全なドキュメント](https://kleinerhacker.github.io/intellij-plugin.inno-setup/)——各 Inno Setup
セクションとそのパラメーターの完全なリファレンスを含む——は、GitHub Pages でホストされているプロジェクトの
MkDocs サイトで利用できます。

ドキュメントサイトをローカルで実行するには：

```bash
# 依存関係をインストール（初回のみ）
cd docs
pip install mkdocs mkdocs-material

# ローカルで配信
mkdocs serve
```

その後、ブラウザで [http://127.0.0.1:8000](http://127.0.0.1:8000) を開きます。

> [API ドキュメント](https://kleinerhacker.github.io/intellij-plugin.inno-setup/dokka/html/) も利用できます。

---

## コントリビューション

[バグ報告](https://github.com/KleinerHacker/intellij-plugin.inno-setup/issues)
と[プルリクエスト](https://github.com/KleinerHacker/intellij-plugin.inno-setup/pulls)を歓迎します。大きな変更に
ついては、まず issue を開いて議論してください。

---

## ライセンス

詳細は [LICENSES](https://kleinerhacker.github.io/intellij-plugin.inno-setup/licences/) を参照してください。
