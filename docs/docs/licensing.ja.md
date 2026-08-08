# ライセンス

## 本プラグイン

Inno Setup JetBrains プラグインは **Apache License, Version 2.0** の下で提供されます。

```
Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

ライセンス全文はリポジトリの
[`LICENSE`](https://github.com/KleinerHacker/intellij-plugin.inno-setup/blob/master/LICENSE) ファイルにあります。

---

## Inno Setup

```
Copyright (C) 1997-2026 Jordan Russell. All rights reserved.
Portions Copyright (C) 2000-2026 Martijn Laan.
```

Inno Setup は [jrsoftware.org](https://jrsoftware.org/isinfo.php) が独自の寛容なライセンスで公開している無償のインストーラー
ビルダーです。ライセンス全文は
[Inno Setup リポジトリ](https://github.com/jrsoftware/issrc/blob/main/license.txt) で参照できます。

**Inno Setup のいかなる部分も本プラグインには同梱されていません。** Inno Setup コンパイラ (`ISCC.exe`)
は別途インストールする必要があり、プラグイン設定で指定したインストールディレクトリから呼び出されます。

### 派生ドキュメント

コード補完、インラインドキュメント、検証に使用されるセクション、ディレクティブ、パラメータ、定数、言語コード、プリプロセッサの各説明は、公式の
[Inno Setup ドキュメント](https://jrsoftware.org/ishelp/) から派生したものであり、Jordan Russell 氏および Martijn Laan
氏が保有する著作権の対象です。

### サンプルスクリプト

統合テストスイートは公式の Inno Setup サンプルスクリプトを用いてプラグインを検証します。これらのスクリプトは本リポジトリの一部では
**ありません**。テスト実行前に固定タグの `jrsoftware/issrc` からビルドディレクトリへダウンロードされ、実行後に削除されます。

---

## 商標および免責事項

本プラグインは非公式のコミュニティ開発プラグインです。Jordan Russell 氏、Martijn Laan 氏および jrsoftware.org
とは提携しておらず、これらによる承認や後援も受けて**いません**。「Inno Setup」および Inno Setup
のロゴは各権利者に帰属し、本プラグインが対象とするソフトウェアを示す目的でのみ使用しています。

本プラグインのロゴは独自に制作したものであり、Inno Setup のロゴから派生したものではありません。

---

## 依存関係

同梱されるサードパーティ依存関係のライセンスは、生成された
[依存関係レポート](licences/index.html) に一覧されています。
