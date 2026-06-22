# `#for`

`#for` は C スタイルのループヘッダーを評価して、ディレクティブを複数回繰り返します。これはプリプロセッサーで
繰り返しのスクリプト内容を生成する方法です。たとえば[配列](arrays.md)の項目ごとに 1 エントリを生成します。

---

## 構文

```ini
#for {Init; Condition; Increment} Directive
```

ヘッダーはセミコロンで区切られた 3 つの部分から成ります。初期化、各反復前にチェックされる条件、各反復後に
評価される増分 — まさに C の `for` ループと同じです。

---

## 説明

各反復で末尾のディレクティブが実行されます。[`#emit`](output.md) や[配列](arrays.md)と組み合わせると、
`#for` は一連のスクリプト行を生成します：

```ini
#dim Langs[3]
#define Langs[0] "en"
#define Langs[1] "de"
#define Langs[2] "fr"

[Languages]
#for {i = 0; i < 3; i++} \
  #emit "Name: """ + Langs[i] + """; MessagesFile: ""compiler:Languages\\" + Langs[i] + ".isl"""
```

バックスラッシュはディレクティブを次の行に継続します（継続文字の変更は [`#pragma spansymbol`](pragma.md)
を参照）。

---

## エディタサポート

ディレクティブキーワードはハイライト、補完（`#` の後）され、付属の ISPP 仕様に対して検証されます。

---

公式の [`#for` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_for.htm)。
