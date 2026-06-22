# `#dim` / `#redim`

`#dim` は**配列**マクロを宣言し、`#redim` は既存の配列の内容を保持したままサイズを変更します。配列を使う
と、[`#for`](for.md) で反復処理できるインデックス付きの値のリストを格納できます。

---

## 構文

```ini
#dim Name[Size]
#redim Name[NewSize]
```

- `#dim` は指定された要素数で配列 `Name` を作成します。
- `#redim` は以前に宣言された配列のサイズを変更します。新しい範囲内の既存要素は保持されます。

---

## 説明

要素は `Name[Index]` でアドレス指定し、[`#define`](define.md) で代入します。典型的なパターンでは、配列を
埋めてから走査します：

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < 3; i++} \
  #pragma message Servers[i]
```

要素数が後でしか分からない場合（たとえば項目を数えた後）は `#redim` を使います：

```ini
#redim Servers[5]   ; 最初の 3 つの値を保持したまま配列を拡張
```

---

## エディタサポート

両方のディレクティブキーワードはハイライト、補完（`#` の後）され、付属の ISPP 仕様に対して検証されます。

---

公式の [`#dim` / `#redim` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm)。
