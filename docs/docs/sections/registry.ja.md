# [Registry]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=registrysection){ .md-button .md-button--primary }

`[Registry]` セクションはインストール中に Windows レジストリキーと値を作成、変更、または削除します。すべての主要なレジストリ値タイプをサポートし、`uninsdeletekey` や `uninsdeletekeyifempty` などのフラグを通じてアンインストール時のレジストリデータの処理を細かく制御します。32 ビットおよび 64 ビットレジストリビューを明示的にターゲットにできます。

---

## Root

`string` · **必須**

レジストリルートハイブ：`HKCU`、`HKLM`、`HKCR`、`HKU`、`HKCC`、または `HKA`（インストールモードに応じて自動）。`32` または `64` を追加すると特定のレジストリビューが強制されます（例：`HKLM64`）。

---

## Subkey

`string` · **必須**

`Root` からの相対レジストリキーパス（例：`Software\My Company\My App`）。

---

## ValueType

`string`

書き込むレジストリ値のタイプ：`none`（キーのみ）、`string`、`expandsz`、`multisz`、`dword`、`qword`、`binary`。

---

## ValueName

`string`

レジストリ値の名前。空のままにするとキーのデフォルト値をターゲットにします。

---

## ValueData

`string`

書き込むデータ。既存の値に追加するには `{olddata}` を使用し、`multisz` 値の行区切りには `{break}` を使用します。

---

## Permissions

`string` · **複数の値**

キーに設定する ACL 権限：`full`、`modify`、`read`。

---

## Flags

`string` · **複数の値**

動作フラグ：`createvalueifdoesntexist`、`deletekey`、`deletevalue`、`dontcreatekey`、`noerror`、`preservestringtype`、`uninsclearvalue`、`uninsdeletekey`、`uninsdeletekeyifempty`、`uninsdeletevalue`。

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

## BeforeInstall

`string`

このエントリーが処理される直前に呼び出される `[Code]` の Pascal プロシージャ名。

---

## AfterInstall

`string`

このエントリーが処理された直後に呼び出される `[Code]` の Pascal プロシージャ名。

---

## MinVersion

`string`

このエントリーが適用される最低 Windows バージョン。`0` を使用すると適用しません。

---

## OnlyBelowVersion

`string`

このエントリーが適用される最高 Windows バージョン（排他的）。`0` を使用すると上限なし。
