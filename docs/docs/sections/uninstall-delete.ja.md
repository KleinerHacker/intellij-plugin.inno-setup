# [UninstallDelete]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=uninstalldeletesection){
.md-button .md-button--primary }

`[UninstallDelete]` セクションはアンインストール中に Inno Setup が最初に追跡したファイルに加えて削除するファイルとディレクトリを列挙します。実行時に作成されたため `[Files]` には一覧されていない生成ファイル、キャッシュ、ログファイル、ユーザーデータディレクトリのクリーンアップに使用します。

---

## Type

`string` · **必須**

削除するもの：`files`（一致するファイルのみ）、`filesandordirs`（ファイルとすべてのサブディレクトリ）、`dirifempty`（ファイルが含まれていない場合のみディレクトリ）。

---

## Name

`string` · **必須**

削除するファイルまたはディレクトリのパスまたはワイルドカードパターン（例：`{app}\*.log` または `{app}\cache`）。

---

## Components

`→ Components` · **複数の値**

リストされたコンポーネントの少なくとも 1 つが選択されている場合にのみ処理されます。

---

## Tasks

`→ Tasks` · **複数の値**

リストされたタスクの少なくとも 1 つがチェックされている場合にのみ処理されます。

---

## Languages

`→ Languages` · **複数の値**

このエントリーを指定された言語に限定します。

---

## Check

`string`

`[Code]` の `Boolean` を返す Pascal 関数名。関数が `True` を返す場合にのみ処理されます。

---

## MinVersion

`string`

このエントリーが適用される最低 Windows バージョン。`0` を使用すると適用しません。

---

## OnlyBelowVersion

`string`

このエントリーが適用される最高 Windows バージョン（排他的）。`0` を使用すると上限なし。
