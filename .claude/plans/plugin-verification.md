# Plugin Verification for Multiple IDEs

## Tasks

### 1. Verification matrix in `plugin/build.gradle.kts`

* Add import `org.jetbrains.intellij.platform.gradle.IntelliJPlatformType`
* Add `pluginVerification { ides { … } }` inside `intellijPlatform`
* Verify IntelliJ IDEA, Rider, CLion and GoLand
* Read the version from `libs.versions.idea`, never hardcoded
* Comment the block and point at the rule file

### 2. Rule `.claude/rules/plugin-verification.md`

* Declare the matrix as authoritative and mandatory to keep current
* Require the four IDEs as a minimum
* Forbid hardcoded versions; require the version catalog
* Require a re-check when `sinceBuild` / `untilBuild` change
* Require `verifyPlugin` to pass without errors

### 3. CI job in `.github/workflows/ci.yml`

* Add job `verify-plugin` gated on `tests`
* JDK 25, gradle cache, `./gradlew verifyPlugin`
* Upload the verifier report as an artifact

### 4. Verification

* Run `build` via agent
* Run `verifyPlugin` via agent
* Fix DEPRECATION / REMOVAL findings

### 5. Out of scope

* No CHANGELOG entry — the change is not user visible
