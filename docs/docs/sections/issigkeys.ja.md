# [ISSigKeys]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=issigkeyssection){ .md-button .md-button--primary }

`[ISSigKeys]` セクションは `.issig` ファイル署名の検証に使用する公開鍵を定義します。これらのキーは `[Files]` の `ISSigAllowedKeys` パラメーターと `issigverify` フラグから参照されます。

*このセクションは Inno Setup 6.5 以降で利用できます。*

---

## Name

`string` · **必須**

このキーエントリーの識別子。`[Files]` エントリーは `ISSigAllowedKeys` を通じてこれを参照します。

---

## Group

`string`

`ISSigAllowedKeys` で複数のキーが識別子を共有できるようにする論理グループ名。

---

## KeyFile

`string`

公開鍵データを含むキーファイルへのパス。

---

## PublicX

`string`

公開 EC 鍵の X 座標の 16 進数エンコード。

---

## PublicY

`string`

公開 EC 鍵の Y 座標の 16 進数エンコード。

---

## KeyID

`string`

キー検索のためにインストーラーに埋め込まれるオプションのコンパイル時キー識別子。

---

## RuntimeID

`string`

キー検索のためにインストール時にインストーラーが使用するオプションのランタイムキー識別子。
