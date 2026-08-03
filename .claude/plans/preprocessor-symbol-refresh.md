# Preprocessor Re-Analysis on Build-Configuration Change

## Tasks

### 1. Tracker in the preprocessor module
- New `IsPreprocessorSymbolTracker`, project service, `SimpleModificationTracker`.
- Placed next to `IsPreprocessorSymbolProvider`.
- API: `getInstance(project)`, `symbolsChanged()`.

### 2. Cache dependency
- `IsPreprocessorBranchAnalysis.analyze()` lists the tracker next to the document.

### 3. Bump on symbol-source changes
- `IsRunConfigurationSelectionListener.refresh()` before the daemon restart.
- `IsBuildConfigurationService.invalidate()` — covers save, delete, replaceAll.
- `IsBuildSettingsConfigurable.apply()` restarts the daemon after writing.

### 4. Settings hint text
- `settings.build.config.comment` without any programming-language comparison.
- Same wording in the ja / ko / zh_CN bundles and the four `settings-build*.md` docs.

### 5. Tests
- `IsPreprocessorBranchAnalysisCacheTest`: invalidation on bump, cache kept without bump.
- `IsBuildConfigurationServiceTest`: save / delete / replaceAll bump the tracker.
- New `IsRunConfigurationSelectionListenerTest`: selected / changed / removed bump the tracker.

### 6. Docs
- `CHANGELOG.md` under `[Unreleased]`, section `Fixed`.
