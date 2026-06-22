# `#sub` / `#endsub`

`#sub` は**サブルーチン** — 繰り返し呼び出せる名前付きディレクティブブロック — を開始し、`#endsub` で
終了します。サブルーチンは [`#for`](for.md) ループの本体として実行されることが最も多いです。

---

## 構文

```ini
#sub Name
  ; ディレクティブ …
#endsub
```

---

## 説明

`#sub Name` と `#endsub` の間にあるものはすべて `Name` の下に保存され、サブルーチンが呼び出されるたびに
実行されます（たとえば `#for` の `Func` パラメータ経由）。サブルーチン内では現在のループ変数が利用できる
ため、呼び出しごとに異なるものを出力できます：

```ini
#dim Files[2]
#define Files[0] "app.exe"
#define Files[1] "help.chm"

#sub EmitFile
  #emit "Source: """ + Files[i] + """; DestDir: ""{app}"""
#endsub

[Files]
#for {i = 0; i < 2; i++; EmitFile}
```

すべての `#sub` は対応する `#endsub` で閉じる必要があります。

---

## エディタサポート

両方のディレクティブキーワードはハイライト、補完（`#` の後）され、付属の ISPP 仕様に対して検証されます。

---

公式の [`#sub` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_sub.htm)。
