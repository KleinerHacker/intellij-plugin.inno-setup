# [Messages]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=messagessection){ .md-button .md-button--primary }

The `[Messages]` section overrides built-in installer UI strings from `Default.isl` or the selected language file. Each
entry uses directive syntax:

```ini
MessageID=Text
german.MessageID=Text
```

The optional language prefix targets a single language declared in `[Languages]`.

---

## Known Message IDs

The plugin ships the standard `Default.isl` message identifiers as known keys. Completion suggests message IDs and, when
used before the dot, the available language prefixes.

---

## Language Prefixes

Language prefixes resolve to `[Languages]` entries. The plugin supports completion, navigation, find usages, rename, and
language flag inlays for these prefixes when the target language can be resolved.

---

## Values

`string`

Message text displayed by Setup or Uninstall. Keep placeholders such as `%1` and `%2` when overriding messages that
expect runtime substitutions.

