---
name: architecture
---

# Architecture

* There are three libraries with the following structure and meaning:
    * `plugin` - The IntelliJ plugin for Inno Setup; contains ONLY IDE specific extensions plus cross-module language features that require BOTH script and preprocessor (e.g. resolving `#include` across file boundaries)
    * `language` - Logical empty node
        * `preprocessor` - Separate language plugin for the preprocessor part of Inno Setup scripts only; contains ONLY language related extensions (including editor ones) that work without knowledge of the other language part
        * `script` - Separate language plugin for the script part of Inno Setup only (pulls in `preprocessor`); contains ONLY language related extensions (including editor ones) that work without knowledge of the other language part
    * `buildSrc` - Additional things for Gradle functionality

## Structure

* The root package is ALWAYS `org.pcsoft.intellij.plugin.inno_setup`
    * For `plugin` ALWAYS the root
    * For `preprocessor` root + `preprocessor`
    * For `script` root + `script`
* Below that there are specialised packages for:
    * `language` - Language units such as parsers, editor features, ...
    * `services` - Injectable services (IntelliJ Platform)
    * `types` - General types
    * `settings` - Settings features, settings pages, ...
    * `build` - Everything around building Inno Setup, e.g. run config, building, ...
* The `*Icons` class collecting all icons of a module MUST ALWAYS reside on the package root level of that
  module, NOT in a specialised package
* For any deviation the user MUST be asked