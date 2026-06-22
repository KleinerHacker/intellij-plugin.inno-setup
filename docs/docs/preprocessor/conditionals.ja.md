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

すべての条件キーワードはハイライト、補完（`#` の後）され、付属の ISPP 仕様に対して検証され、制御式は
解析・型チェックされます。

---

公式の [`#if` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_if.htm)。
