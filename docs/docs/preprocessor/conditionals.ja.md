# `#if` / `#elif` / `#else` / `#endif` および `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`

条件ディレクティブは、コンパイル時にスクリプトの一部を含めるか除外します。開始ディレクティブと対応する
`#endif` の間にあるものは、条件が成り立つときだけ出力されます。

---

## 構文

```ini
#if Expression
#elif Expression
#else
#endif

#ifdef Name
#ifndef Name
#ifexist "filename"
#ifnexist "filename"
```

---

## 説明

- **`#if` / `#elif` / `#else` / `#endif`** は整数式（0 以外は真）で駆動される if/else-if/else 連鎖を
  形成します。`#elif` と `#else` は省略可能で、すべてのブロックは `#endif` で閉じる必要があります。
- **`#ifdef` / `#ifndef`** はマクロが定義されている（いない）かをテストします。`#if defined(Name)` と
  `#if !defined(Name)` の短縮形です。
- **`#ifexist` / `#ifnexist`** はファイルがディスク上に存在する（しない）かをテストします。

```ini
#define Beta

#ifdef Beta
  #define AppSuffix " (Beta)"
#else
  #define AppSuffix ""
#endif

#if VER >= 0x06000000
  ; 新しい Inno Setup バージョン専用
#endif

#ifexist "extra\\readme.txt"
  #include "extra\\readme.txt"
#endif
```

---

## エディタサポート

- **ハイライトと補完** — すべての条件キーワードはハイライト、補完（`#` の後）され、付属の ISPP 仕様に
  対して検証されます。
- **条件式** — `#if` / `#elif` の条件は [`#define`](define.md) の値と同じ完全な ISPP 式です。演算子は
  ハイライトされ、構文エラーと型エラーが該当トークン上に報告され、式の補完候補（他の `#define`、定義済み
  変数、組み込み関数）が条件内でも提示されます。
- **参照** — 条件内の識別子は `#define` 宣言に解決されるため、定義へ移動（**Ctrl+B** / **Cmd+B**）、
  使用箇所の検索（**Alt+F7**）、リネームが機能します。未知の名前は*未解決の参照*エラーとして
  マークされます（`#define` と同様）。`defined(Name)` は例外で、その引数は未定義でも構いません。
- **`#ifdef` / `#ifndef`** — 名前はその `#define` 宣言に解決され（定義へ移動、使用箇所の検索、リネーム）、
  補完で `#define` 名が提示されます。`#if` の条件とは異なり、未知の名前は**エラーになりません**。未定義の
  マクロを判定することこそが `#ifdef` / `#ifndef` の目的だからです。
- **`#ifexist` / `#ifnexist`** — ファイル名は完全な ISPP **文字列**式です。演算子と型エラーが検証され
  （値は文字列でなければなりません）、識別子は `#define` に解決されます。（これらのディレクティブはスク
  リプトファイルだけでなくディスク上の*任意の*ファイルを判定できるため、ファイル名補完は提示されません。）
- **真偽値リテラル** — ISPP には真偽値が無いため、条件で直接使用された `true` / `false` / `yes` / `no`
  は**黄色**で表示され、警告が付きます（その語は未定義の識別子 `0` として扱われます）。
- **構造の検証** — すべての開始ディレクティブ（`#if` / `#ifdef` / `#ifndef` / `#ifexist` / `#ifnexist`）は
  ファイル末尾までに `#endif` で閉じる必要があります。閉じられていない開始、開いたブロックの無い
  `#elif` / `#else` / `#endif`、`#else` の後の `#elif` はすべてエラーになります。条件の無い
  `#if` / `#elif` もエラーです。
- **折りたたみ** — 完全な `#if … #endif` ブロックは、単一のセクション内に完全に収まる場合、または
  どのセクションにも属さない場合に折りたためます（セクションヘッダーをまたぐブロックは折りたためません）。

---

公式の [`#if` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm)。
