# [Code]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=scriptintro){
.md-button .md-button--primary }

`[Code]` セクションは Inno Setup のスクリプトエンジンの全機能が活躍する場所です。他のすべてのセクションとは異なり、
`Key=Value` や `Key: Value` 構文を使用しません——*RemObjects Pascal Script* を使用してインストーラーが実行時にコンパイルおよび実行する自由形式の
Pascal ソースコードが含まれます。

`InitializeSetup`、`NextButtonClick`、`CurStepChanged`、`PrepareToInstall`
などのイベント関数を通じて、インストールウィザードのほぼすべての段階をインターセプトし、カスタムチェックの実行、ファイルのダウンロード、カスタムページの表示、レジストリへの書き込みなどを行うことができます。

!!! info "パラメーターなし"
`[Code]` セクションには構造化されたパラメーターはありません。その内容全体が Pascal
ソースコードです。利用可能なすべてのイベント関数、組み込みプロシージャ、サポートされる Pascal 言語機能を含む完全な API
サーフェスについては、[Inno Setup スクリプトリファレンス](https://jrsoftware.org/ishelp/index.php?topic=scriptintro)
を参照してください。

!!! note "`[Code]` 内では不適切なエディター支援機能は動作しません"
内容が純粋な Pascal であるため、このプラグインは `[Code]` 内で ISS 固有のエディター機能を意図的に無効化します。クイックドキュメント、セクション間参照およびカスタムメッセージ参照、`"`
の自動補完（Pascal の文字列は `'` を使用します）、*Flip parameters* インテンションはいずれも提供されません。プリプロセッサは有効なままです。Inno Setup は
`[Code]` 内でも ISPP を評価するため、`#…` ディレクティブ行とインラインの `{#…}` 出力はドキュメントと参照を保持します。
