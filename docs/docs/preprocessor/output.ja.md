# `#emit` / `#expr` / `#insert` / `#append`

これらのディレクティブは式を評価し、その結果をどう扱うかを決めます。インライン形式 `{#…}` の明示的な
対応物です。

---

## 構文

```ini
#emit Expression
#expr Expression
#insert Expression
#append Expression
```

---

## 説明

- **`#emit`** は式を評価し、その値をスクリプトの 1 行として出力に書き込みます。通常の行内のインライン形式
  `{#expr}` は `{#emit expr}` の短縮形です。
- **`#expr`** は式を**副作用**のためだけに評価し（たとえば関数呼び出しやマクロへの代入）、結果は破棄します。
  何も出力されません。
- **`#insert`** と **`#append`** は、現在の出力行を基準にした位置に出力を配置します。`#insert` はその前、
  `#append` はその後で、セクションをプログラム的に生成するときに便利です。

```ini
#define AppExe "MyApp.exe"

[Run]
#emit "Filename: ""{app}\\" + AppExe + """; Flags: nowait"

; 副作用のために評価し、何も出力しない
#expr Local[0] = GetEnv("BUILD_ID")
```

---

## エディタサポート

4 つのディレクティブキーワードはすべてハイライト、補完（`#` の後）され、付属の ISPP 仕様に対して検証され、
式は解析・型チェックされます。

---

公式の [`#emit` / `#expr` ドキュメント :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_emit.htm)。
