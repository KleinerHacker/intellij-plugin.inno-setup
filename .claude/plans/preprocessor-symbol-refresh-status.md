# Status: Preprocessor Re-Analysis on Build-Configuration Change

| Task | Status |
|------|--------|
| 1. Tracker in the preprocessor module | done |
| 2. Cache dependency | done |
| 3. Bump on symbol-source changes | done |
| 4. Settings hint text | done |
| 5. Tests | done |
| 6. Docs | done |
| Build / test | done — BUILD SUCCESSFUL, all tests green |
| verifyPlugin | done — verdict Compatible, no errors |

## Open (pre-existing, outside this change)

`verifyPlugin` reports three deprecated usages that already existed before this change:
`ToolWindowFactory.isApplicable` / `isDoNotActivateOnStart` (`IsBuildToolWindowFactory`) and the
`LineMarkerInfo` constructor (`IsSetupSectionGutterIconProvider.collectSlowLineMarkers`).
