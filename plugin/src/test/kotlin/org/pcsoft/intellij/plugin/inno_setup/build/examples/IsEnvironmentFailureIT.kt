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

package org.pcsoft.intellij.plugin.inno_setup.build.examples

import junit.framework.TestCase

/**
 * A stand-alone test case that always fails with a given message — used by [IsExampleScriptBuildIT.suite] to
 * report a broken environment (missing example corpus, missing `ISCC.exe`) as an ordinary failing test.
 *
 * It exists as a **named, `IT`-suffixed** class on purpose. The suites are separated by class name
 * (`excludeTestsMatching("*IT")` / `includeTestsMatching("*IT")` in `plugin/build.gradle.kts`), and that
 * filter sees the class of the test case object, not the name of the enclosing suite. As an anonymous
 * `object : TestCase` this case was called `…IsExampleScriptBuildIT$Companion$failing$1`, which does not end
 * in `IT` — so it slipped past the exclusion and failed the developer suite, which deliberately never
 * downloads the corpus.
 *
 * @constructor Creates the case named [name] that fails with [message].
 */
class IsEnvironmentFailureIT(name: String, private val message: String) : TestCase(name) {

    /** Fails unconditionally with the message this case was created with. */
    override fun runTest() {
        fail(message)
    }
}
