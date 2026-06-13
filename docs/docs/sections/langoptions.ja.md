# [LangOptions]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=langoptionssection){ .md-button .md-button--primary }

`[LangOptions]` セクションは言語固有の表示設定を定義します。`.isl` 言語ファイルでは必須で、スクリプトで宣言された言語のオプションをオーバーライドするためにスクリプト内でも使用できます。スクリプトでは、ディレクティブ名に言語名をプレフィックスとして付けることができます（例：`german.DialogFontName=Segoe UI`）。

---

## LanguageName

`string` · **.isl では必須**

言語選択ダイアログに表示される言語のネイティブ名（例：`Deutsch`）。

---

## LanguageID

`integer` · **.isl では必須**

自動言語検出に使用される Windows 言語識別子。通常は Pascal スタイルの 16 進数で記述します（例：英語（米国）は `$0409`、ドイツ語（ドイツ）は `$0407`）。補完はバンドルされた Windows LCID リストを使用します。

---

## LanguageCodePage

`integer`

メッセージファイル内の非 Unicode テキストの変換に使用するコードページ。ファイルが Unicode または ASCII テキストのみを含む場合は `0` を使用します。

---

## DialogFontName

`string`

ほとんどのウィザードテキストに使用されるフォント。空のままにすると Segoe UI がデフォルトになります。

---

## DialogFontSize

`integer`

ダイアログフォントのポイントサイズ。デフォルト：`9`。

---

## DialogFontBaseScaleWidth

`integer`

ダイアログフォントに対してダイアログコントロールをスケーリングするために使用される基本幅（ピクセル単位）。デフォルト：`7`。

---

## DialogFontBaseScaleHeight

`integer`

ダイアログフォントに対してダイアログコントロールをスケーリングするために使用される基本高さ（ピクセル単位）。デフォルト：`15`。

---

## WelcomeFontName

`string`

ウェルカムページとセットアップ完了ページの大きな見出しに使用されるフォント。

---

## WelcomeFontSize

`integer`

ウェルカムフォントのポイントサイズ。デフォルト：`14`。

---

## RightToLeft

`integer`

右から左への言語には `1`、左から右への言語には `0` を設定します。

---

## 削除されたディレクティブ

`TitleFontName`、`TitleFontSize`、`CopyrightFontName`、`CopyrightFontSize` は Inno Setup 6.4 で削除されました。プラグインは古いスクリプトとの互換性のために削除バージョンとともにマークして保持しています。
