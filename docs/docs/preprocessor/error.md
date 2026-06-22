# `#error`

`#error` stops compilation immediately and reports the given message. Use it to fail the build when a
required condition is not met — for example a missing macro or an unsupported configuration.

---

## Syntax

```ini
#error Message
```

The message is the rest of the line. It is shown to the user and the compile is aborted.

---

## Description

`#error` is normally guarded by a [conditional](conditionals.md) so it only triggers in the bad case:

```ini
#ifndef AppVersion
  #error AppVersion must be defined before including this file
#endif
```

Unlike `#pragma error`, which takes a string **expression**, `#error` treats the remainder of the line as a
plain message.

---

## Editor support

The directive keyword is highlighted, completed (after `#`) and validated against the bundled ISPP
specification.

---

See the official [`#error` documentation :octicons-link-external-16:](https://jrsoftware.org/ispphelp/topic_error.htm).
