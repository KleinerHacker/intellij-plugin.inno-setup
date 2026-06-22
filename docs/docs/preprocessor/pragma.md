# `#pragma`

`#pragma` controls the preprocessor itself. Instead of producing output, it takes a **sub-command** that
adjusts how the preprocessor reads, parses or reports on the script. The plugin knows every sub-command
from its bundled specification and validates the argument that follows.

---

## Syntax

```ini
#pragma <sub-command> [arguments]
```

The first word after `#pragma` is the sub-command; everything after it is its argument. An unknown
sub-command is flagged as an error, as is a missing or ill-typed argument.

---

## Sub-commands

| Sub-command | Argument | Purpose |
|-------------|----------|---------|
| `option` | option flags | General reading/output options of the preprocessor |
| `parseroption` | option flags | Options that control expression parsing |
| `message` | string | Print an informational message to the compiler window |
| `warning` | string | Print a warning to the compiler window |
| `error` | string | Show an error message in a dialog box |
| `verboselevel` | integer (0–10) | Set the verbosity threshold for messages |
| `inlinestart` | string | Set the opening delimiter for inline directives (default `{#`) |
| `inlineend` | string | Set the closing delimiter for inline directives (default `}`) |
| `include` | string | Set the semicolon-separated search paths for included files |
| `spansymbol` | string | Set the line-continuation character (first character only) |

---

## Option flags: `option` and `parseroption`

`option` and `parseroption` take one or more flags of the form `-<letter>(+|-)`, separated by spaces.
A `+` turns the option on, a `-` turns it off. The plugin reports an unknown letter or a malformed flag
(missing dash or sign) as an error.

### `option` flags

| Flag | Default | Meaning |
|------|---------|---------|
| `c` | on | Output to compiler |
| `e` | on | Emit empty lines |
| `v` | off | Verbose mode |

### `parseroption` flags

| Flag | Default | Meaning |
|------|---------|---------|
| `b` | on | Short-circuit boolean evaluation |
| `m` | off | Short-circuit multiplication evaluation |
| `p` | on | Pascal-style string literals |
| `u` | off | Allow undeclared identifiers |

```ini
#pragma option -v+            ; enable verbose output
#pragma parseroption -b- -u+  ; disable boolean short-circuit, allow undeclared identifiers
```

---

## Expression sub-commands

`message`, `warning`, `error`, `include`, `inlinestart`, `inlineend` and `spansymbol` take a **string
expression**; `verboselevel` takes an **integer expression**. The plugin parses and type-checks the
argument with the same engine used for `#define`, so a wrong type (for example a number where a string is
expected) is flagged, and `verboselevel` must be in the range 0–10.

```ini
#define BuildId 42
#pragma message "Building configuration #" + Str(BuildId)
#pragma verboselevel 9
#pragma inlinestart "$("
#pragma inlineend ")"
```

Identifiers inside these expressions reference your `#define`s just like they do inside a `#define` value —
they resolve, support go-to-definition, Find Usages and rename, and an unknown name is flagged as an
unresolved reference.

---

## Editor support

- **Validation** of the sub-command name, of the option-flag letters and form, and of the argument type
- **Completion** of sub-commands after `#pragma `, and of option flags after `#pragma option `/
  `#pragma parseroption `
- **Reference resolution, Find Usages and rename** for `#define`s used inside an expression argument

---

See the official [`#pragma` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_pragma.htm)
for the full reference.
