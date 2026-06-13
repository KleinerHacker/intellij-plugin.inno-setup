# [LangOptions]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=langoptionssection){
.md-button .md-button--primary }

`[LangOptions]` 节定义特定语言的显示设置。它在 `.isl` 语言文件中是必需的，也可以在脚本中用于覆盖已声明语言的选项。在脚本中，指令名称可以以语言名称为前缀，例如 `german.DialogFontName=Segoe UI`。

---

## LanguageName

`string` · **在 .isl 中必需**

语言选择对话框中显示的语言本地名称，例如 `Deutsch`。

---

## LanguageID

`integer` · **在 .isl 中必需**

用于自动语言检测的 Windows 语言标识符，通常以 Pascal 风格的十六进制写出，例如英语（美国）为 `$0409`，德语（德国）为 `$0407`。补全使用捆绑的 Windows LCID 列表。

---

## LanguageCodePage

`integer`

用于转换消息文件中非 Unicode 文本的代码页。文件仅包含 Unicode 或 ASCII 文本时使用 `0`。

---

## DialogFontName

`string`

用于大多数向导文本的字体。留空时默认为 Segoe UI。

---

## DialogFontSize

`integer`

对话框字体的磅值。默认：`9`。

---

## DialogFontBaseScaleWidth

`integer`

用于相对于对话框字体缩放对话框控件的基本宽度（像素）。默认：`7`。

---

## DialogFontBaseScaleHeight

`integer`

用于相对于对话框字体缩放对话框控件的基本高度（像素）。默认：`15`。

---

## WelcomeFontName

`string`

用于欢迎页和安装完成页大标题的字体。

---

## WelcomeFontSize

`integer`

欢迎字体的磅值。默认：`14`。

---

## RightToLeft

`integer`

从右到左语言设置为 `1`，从左到右语言设置为 `0`。

---

## 已删除的指令

`TitleFontName`、`TitleFontSize`、`CopyrightFontName` 和 `CopyrightFontSize` 在 Inno Setup 6.4 中被删除。插件保留它们并标注删除版本，以兼容旧版脚本。
