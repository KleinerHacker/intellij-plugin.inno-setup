/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.test

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.ThrowableRunnable

/**
 * Base class for all platform-fixture tests: a drop-in replacement for [BasePlatformTestCase] that enforces a
 * global per-method timeout (see [runWithMethodTimeout]) so no single test can hang the suite.
 *
 * `runBare()` is `final` in `UsefulTestCase`, so the timeout wraps [runTestRunnable] — the hook that executes
 * the actual test body (between `setUp`/`tearDown`).
 */
abstract class IsTimedBasePlatformTestCase : BasePlatformTestCase() {

    @Throws(Throwable::class)
    override fun runTestRunnable(testRunnable: ThrowableRunnable<Throwable>) {
        runWithMethodTimeout { super.runTestRunnable(testRunnable) }
    }
}
