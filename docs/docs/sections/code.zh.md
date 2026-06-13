# [Code]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=scriptintro){ .md-button .md-button--primary }

`[Code]` 节是 Inno Setup 脚本引擎全部功能发挥之处。与其他每个节不同，它不使用 `Key=Value` 或 `Key: Value` 语法——它包含由安装程序使用 *RemObjects Pascal Script* 在运行时编译和执行的自由格式 Pascal 源代码。

通过 `InitializeSetup`、`NextButtonClick`、`CurStepChanged` 和 `PrepareToInstall` 等事件函数，您可以拦截安装向导的几乎每个阶段，执行自定义检查、下载文件、显示自定义页面、写入注册表等等。

!!! info "无参数"
`[Code]` 节没有结构化参数。其全部内容是 Pascal 源代码。请参阅 [Inno Setup 脚本参考](https://jrsoftware.org/ishelp/index.php?topic=scriptintro)，获取完整的 API 界面，包括所有可用的事件函数、内置过程和支持的 Pascal 语言功能。
