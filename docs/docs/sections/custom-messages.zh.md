# [CustomMessages]

[:octicons-link-external-16: Inno Setup 参考](https://jrsoftware.org/ishelp/index.php?topic=custommessagessection){
.md-button .md-button--primary }

`[CustomMessages]` 节定义项目特定的可本地化字符串。这些字符串可通过 `{cm:MessageName}` 常量从其他节和 Pascal 代码中引用。

```ini
[CustomMessages]
WelcomeText=Welcome to My App
german.WelcomeText=Willkommen bei My App
```

---

## 消息名称

`string`

此节中没有预定义键。消息名称由脚本作者选择，可选地以语言名称为前缀，例如 `german.WelcomeText`。

---

## 引用

`{cm:MessageName}` 解析到匹配的 `[CustomMessages]` 条目。该插件支持 `{cm:` 之后的补全、查找用法、重命名重构和未解析引用高亮显示。

重命名消息时，插件会同步保持语言变体和 `{cm:...}` 用法。
