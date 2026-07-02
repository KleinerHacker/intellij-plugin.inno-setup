# [CustomMessages]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=custommessagessection){
.md-button .md-button--primary }

The `[CustomMessages]` section defines project-specific localizable strings. These strings can be referenced from other
sections and Pascal code through the `{cm:MessageName}` constant.

```ini
[CustomMessages]
WelcomeText=Welcome to My App
german.WelcomeText=Willkommen bei My App
```

---

## Message Names

`string`

There are no predefined keys in this section. The message name is chosen by the script author and can optionally be
prefixed with a language name, e.g. `german.WelcomeText`.

---

## References

`{cm:MessageName}` resolves to the matching `[CustomMessages]` entry. The plugin supports completion after `{cm:`, find
usages, rename refactoring, and unresolved-reference highlighting.

When a message is renamed, the plugin keeps language variants and `{cm:...}` usages in sync.

