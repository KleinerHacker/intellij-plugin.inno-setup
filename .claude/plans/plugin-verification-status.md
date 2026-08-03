# Plugin Verification for Multiple IDEs — Status

| # | Task | Status |
|---|------|--------|
| 1 | Verification matrix in `plugin/build.gradle.kts` | DONE |
| 2 | Rule `.claude/rules/plugin-verification.md` | DONE |
| 3 | CI job `verify-plugin` in `.github/workflows/ci.yml` | DONE |
| 4 | Release job `verify-plugin` in `.github/workflows/release.yml` | DONE |
| 5 | Fix broken `integrationTest` task reference in both workflows | DONE |
| 6 | Run `build` + `verifyPlugin` | DONE |
| 7 | No CHANGELOG entry (out of scope) | DONE |

## Verification Result (2026-08-03)

* `build` — SUCCESS
* `verifyPlugin` — SUCCESS, all four IDEs verified:
  * `IU-262.8665.258` — Compatible
  * `RD-262.8665.328` — Compatible
  * `CL-262.8665.262` — Compatible
  * `GO-262.8665.270` — Compatible
* No compatibility problems, no "scheduled for removal" usages in any IDE.

## Open Follow-Up

5 pre-existing DEPRECATED API usages (identical in all four IDEs), not introduced by this change:

* `ToolWindowFactory.isApplicable(Project)` — overridden + invoked in `IsBuildToolWindowFactory`
* `ToolWindowFactory.isDoNotActivateOnStart()` — overridden + invoked in `IsBuildToolWindowFactory`
* `LineMarkerInfo(T, TextRange, Icon, Function, GutterIconNavigationHandler, Alignment)` — invoked in
  `IsSetupSectionGutterIconProvider.collectSlowLineMarkers`

Per CLAUDE.md these MUST be adjusted; requires a separate change to production code.
