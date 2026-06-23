# `#include` / `#file`

`#include` 在编译时将另一个文件的内容粘贴到脚本中，`#file` 读取文件并将其内容供预处理器使用。两者都将
外部内容引入脚本。

---

## 语法

```ini
#include "filename.iss"
#include <filename.iss>
#file "data.txt"
```

- `#include "file"` 相对于包含它的脚本解析（绝对路径按原样使用）；尖括号形式会搜索配置的包含路径。
- `#file "file"` 读取文件并返回一个临时文件名，供其他指令引用。

---

## 使用 `#include`

插件将 `#include` 视为一等引用：

- **转到文件** —— **Ctrl+B** / **Cmd+B** 跳转到被引用的文件；输入时会补全路径。
- **内容内联预览** —— 被包含文件解析后的内容会以内嵌提示显示在 `#include` 行下方（保留文件的换行），
  点击可跳转到该文件。可在[编辑器设置](../settings-editor.md)页面开关（默认启用）。当文件无法找到或
  无法读取时不显示预览。
- **自动路径更新** —— 在 IDE 中重命名或移动目标文件会更新 `#include` 路径。
- **内联 `#include` 内容** —— `#include` 行上的 **Alt+Enter** 意图会用文件内容（仅一层）替换该行。随后
  会询问是否删除已内联的文件。
- **将选择内容提取为 `#include` 文件** —— 选择若干行并将其移动到当前脚本旁的新文件中，选择内容会被替换为
  对该新文件的 `#include`。
- **显示有效脚本** —— 在只读标签页中打开完全解析了 include 的脚本。

```ini
#include "common\\settings.iss"

[Setup]
AppName={#MyAppName}      ; 在被包含文件内定义
```

---

## 验证

`#include` 行本身会被检查：缺失或不存在的文件，以及非字面量或空路径，都会被标记为错误。在被包含文件
**内部**检测到的问题（未知指令、标志、未定义常量）会显示在包含脚本的 `#include` 行上，必需节检查也会
考虑 include 所贡献的内容。

---

参阅官方
[`#include` :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_include.htm) 和
[`#file` :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_file.htm) 文档。
