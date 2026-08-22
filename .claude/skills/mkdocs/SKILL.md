---
name: mkdocs
description: Layout of the MkDocs documentation under docs/. Use when editing docs/, mkdocs.yml, assets or stylesheets.
---

# MkDocs

* MkDocs MUST be integrated under `docs`
* Structure:
    * `docs/mkdocs.yml` - Root file
    * `docs/docs` - *.MD files
    * `docs/docs/assets` - Further asset files (MUST reside inside `docs_dir` so that MkDocs ships them)
    * `docs/docs/stylesheets` - Additional CSS files
* The documentation MUST be checked after every change and adjusted if necessary
* All Inno Setup documentation is available here: `https://jrsoftware.org/ishelp/`
