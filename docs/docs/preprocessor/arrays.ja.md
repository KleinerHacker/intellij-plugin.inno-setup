# `#dim` / `#redim`

`#dim` は**配列**マクロを宣言し、`#redim` は既存の配列の内容を保持したままサイズを変更します。配列を使う
と、[`#for`](for.md) で反復処理できるインデックス付きの値のリストを格納できます。

---

## 構文

```ini
#dim [private | protected | public] Name[Size] [{ Init, Init, ... }]
#redim [private | protected | public] Name[NewSize]
```

- `Size` / `NewSize` は整数式です（リテラル、他の `#define`、`DimOf(...)` など）。
- `#dim` はその要素数で配列 `Name` を作成します。各要素は最初は**void**（空）です。
- 任意の `{ ... }` リストは先頭の要素を順に初期化します（`{1, 2, 3}` はインデックス `0`〜`2` を埋めます）。
- `#redim` は以前に宣言された配列のサイズを変更します。新しい範囲内の既存要素は保持され、新しく追加された
  要素は void です。
- 任意のスコープキーワード（`private` / `protected` / `public`）は、[`#define`](define.md) と同じく可視性を
  制御します。

---

## 説明

配列は**ゼロ始まり**です。サイズ `N` の配列は有効なインデックス `0 … N-1` を持ちます。要素は `Name[Index]`
でアドレス指定し、[`#define`](define.md) で代入します。典型的なパターンでは、配列を埋めてから走査します：

```ini
#dim Servers[3]
#define Servers[0] "alpha"
#define Servers[1] "beta"
#define Servers[2] "gamma"

#for {i = 0; i < DimOf(Servers); i++} \
  #pragma message Servers[i]
```

インライン初期化子を使うとより簡潔に書けます：

```ini
#dim Servers[3] {"alpha", "beta", "gamma"}
```

要素の値は他の値と同様に式の中で扱えます。読み取り、結合、他のマクロの参照も可能です：

```ini
#define Base 10
#dim Offsets[2] {Base, Base + 5}
#if Offsets[1] > Offsets[0]
  ; ...
#endif
```

要素数が後でしか分からない場合（たとえば項目を数えた後）は `#redim` を使います：

```ini
#redim Servers[5]   ; 最初の 3 つの値を保持したまま配列を拡張
```

`DimOf(Name)` は配列の現在の要素数を返します。

---

## エディタサポート

- `#dim` / `#redim` キーワード、スコープキーワード、配列名はハイライトされ、付属の ISPP 仕様に対して検証
  されます。`[` と `]` は対応する括弧ペアとして照合されます。
- 配列名は補完で提示されます：`#redim ` の後（既存の配列）と式の中（宣言済みの配列、末尾に `[]` 付き）。
- `Name[Index]`、`#redim Name`、`#define Name[Index]`、`DimOf(Name)` はすべて、元の `#dim` とともにナビ
  ゲート（Ctrl/Cmd-クリック）およびリネームできます。
- 要素の値は `#dim`/`#define` をまたいで静的に評価できます（ドキュメントポップアップなど）。

次の誤りはエラーとして報告されます：

- 配列**ではない**名前へのインデックス指定（`Foo` が通常の `#define` の場合の `Foo[0]`）；
- 式の中で配列名をインデックス**なし**で使用（`Servers[i]` ではなく `Servers`）；
- **非整数**の配列インデックスまたはサイズ；
- `#dim` で宣言されていない配列の `#redim`；
- 宣言サイズと一致しない要素数のインライン初期化子；
- 静的に**範囲外**のインデックス（サイズ 3 の配列に対する `#define Servers[9]` や `Servers[9]`）。

動的なインデックス（ループ変数など）は誤検出を避けるため、意図的に**フラグ付けされません**。

---

公式の [`#dim` / `#redim` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_dim.htm)。
