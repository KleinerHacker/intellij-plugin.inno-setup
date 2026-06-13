# [Tasks]

[:octicons-link-external-16: Inno Setup リファレンス](https://jrsoftware.org/ishelp/index.php?topic=taskssection){ .md-button .md-button--primary }

`[Tasks]` セクションは、ユーザーが*追加タスクの選択*ウィザードページで有効または無効にできるオプションアクションを定義します——デスクトップショートカットの作成やコンテキストメニューエントリーの追加など。タスクはチェックボックスとして表示され、グループ内で `exclusive` とマークされた場合はラジオボタンとして表示されます。他のセクションのエントリーは `Tasks` パラメーターを通じてタスクにリンクされます。

---

## Name

`string` · **必須**

このタスクの内部識別子。サブタスクにはバックスラッシュ表記を使用（例：`desktopicon\user`）。

---

## Description

`string` · **必須**

ウィザードでチェックボックスまたはラジオボタンの横に表示されるラベル。

---

## GroupDescription

`string`

関連タスクのグループの上に表示されるオプションの見出し。

---

## Components

`→ Components` · **複数の値**

リストされたコンポーネントの少なくとも 1 つが選択されている場合にのみ、このタスクが表示されます。

---

## Flags

`string` · **複数の値**

動作フラグ：`checkablealone`、`checkedonce`、`dontinheritcheck`、`exclusive`、`restart`、`unchecked`。

---

## Check

`string`

`[Code]` の `Boolean` を返す Pascal 関数名。関数が `True` を返す場合にのみ、このエントリーが処理されます。

---

## Languages

`→ Languages` · **複数の値**

このエントリーを指定された言語に限定します。
