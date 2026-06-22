# `#error`

`#error` はコンパイルを即座に停止し、指定されたメッセージを報告します。必須条件が満たされない場合 —
たとえばマクロの欠落やサポートされない構成 — にビルドを失敗させるために使います。

---

## 構文

```ini
#error Message
```

メッセージは行の残りの部分です。ユーザーに表示され、コンパイルは中止されます。

---

## 説明

`#error` は通常、悪いケースでのみ発動するように[条件](conditionals.md)で保護されます：

```ini
#ifndef AppVersion
  #error AppVersion must be defined before including this file
#endif
```

文字列**式**を取る `#pragma error` とは異なり、`#error` は行の残りをプレーンなメッセージとして扱います。

---

## エディタサポート

ディレクティブキーワードはハイライト、補完（`#` の後）され、付属の ISPP 仕様に対して検証されます。

---

公式の [`#error` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_error.htm)。
