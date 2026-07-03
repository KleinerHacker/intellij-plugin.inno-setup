# `#undef`

`#undef` は以前に [`#define`](define.md) で宣言したマクロを削除します。これ以降、その名前は定義されなく
なります。`defined(Name)` は false になり、その後のその名前の使用は未定義として扱われます。

---

## 構文

```ini
#undef [Scope] Name
```

名前の前にオプションのスコープキーワード（`public`、`protected`、`private`）を付けることができます
（[`#define`](define.md) と同様）。

---

## 説明

`#undef` は通常、値を再定義したり機能フラグをクリアしたりするために条件と組み合わせて使われます：

```ini
#define EnableLogging
; … 後で …
#undef EnableLogging      ; これ以降、機能フラグは無くなる

#ifdef EnableLogging
  ; もう出力されない
#endif
```

定義されていない名前を `#undef` しても効果はありません。`#undef` は、名前が現在定義されているかをテストする
[条件ディレクティブ](conditionals.md)と組み合わせると最も有用です。

---

## エディタサポート

- ディレクティブキーワード（およびオプションのスコープキーワード）はハイライト、補完（`#` の後）され、
  付属の ISPP 仕様に対して検証されます。
- `#undef Name` の名前は対応する `#define` に**解決されます** — 定義へ移動（**Ctrl+B** / **Cmd+B**）、
  使用箇所の検索（**Alt+F7**）、リネームが `#define`・`#undef`・すべての `{#Name}` 使用箇所を同期します。
- `#undef ` の後の補完では、スコープキーワードと先に定義されたマクロ名が提示されます。
- 対応する `#define` が**ない** `#undef` は何もしません。その名前はグレー表示され、ディレクティブを
  削除するクイックフィックスが提供されます。

---

公式の [`#define` /
`#undef` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_define.htm)。
