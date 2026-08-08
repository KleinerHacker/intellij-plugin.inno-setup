# Licensing

## This Plugin

The Inno Setup JetBrains plugin is licensed under the **Apache License, Version 2.0**.

```
Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

The full license text is available in the
[`LICENSE`](https://github.com/KleinerHacker/intellij-plugin.inno-setup/blob/master/LICENSE) file of the repository.

---

## Inno Setup

```
Copyright (C) 1997-2026 Jordan Russell. All rights reserved.
Portions Copyright (C) 2000-2026 Martijn Laan.
```

Inno Setup is a free installer builder published under its own permissive license by
[jrsoftware.org](https://jrsoftware.org/isinfo.php). Its full license text is available in the
[Inno Setup repository](https://github.com/jrsoftware/issrc/blob/main/license.txt).

**No part of Inno Setup is distributed with this plugin.** The Inno Setup compiler (`ISCC.exe`) has to be installed
separately and is invoked from its installation directory, which is configured in the plugin settings.

### Derived Documentation

The section, directive, parameter, constant, language-code and preprocessor descriptions shipped with the plugin —
used for code completion, inline documentation and validation — are derived from the official
[Inno Setup documentation](https://jrsoftware.org/ishelp/) and are subject to its copyright, held by Jordan Russell and
Martijn Laan.

### Example Scripts

The integration test suite validates the plugin against the official Inno Setup example scripts. Those scripts are
**not** part of this repository: they are downloaded from a pinned `jrsoftware/issrc` tag into a build directory before
the test run and removed again afterwards.

---

## Trademarks & Disclaimer

This is an unofficial, community-developed plugin. It is **not** affiliated with, endorsed by, or sponsored by Jordan
Russell, Martijn Laan or jrsoftware.org. "Inno Setup" and the Inno Setup logo are the property of their respective
owners and are used here only to identify the software this plugin supports.

The plugin logo is an original work and is not derived from the Inno Setup logo.

---

## Dependencies

The licenses of all bundled third-party dependencies are listed in the generated
[dependency report](licences/index.html).
