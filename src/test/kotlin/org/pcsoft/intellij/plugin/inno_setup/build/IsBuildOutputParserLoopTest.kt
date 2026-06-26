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

package org.pcsoft.intellij.plugin.inno_setup.build

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedTestCase

/**
 * Pure (no IntelliJ platform) check that [IsBuildOutputParser.parseLine] — the code behind
 * `IsBuildOutputFilter` and `IsBuildOutputFilterTest#testPlainLineHasNoLink` — terminates immediately and
 * does not catastrophically backtrack on any input, including long/adversarial ones.
 */
class IsBuildOutputParserLoopTest : IsTimedTestCase() {

    fun testPlainLineParsesInstantly() {
        val r = IsBuildOutputParser.parseLine("Compiling setup.iss...\n")
        assertTrue(r is IsBuildOutputParser.Line.Output)
    }

    fun testAdversarialInputsDoNotBacktrackForever() {
        val inputs = listOf(
            "Compiling setup.iss...",
            "error on line 23 in " + "a".repeat(20_000),                 // POSITIONAL without ": " tail
            "error on line 23 in " + "a/".repeat(10_000) + " : x",       // many separators, late colon
            "warning " + " ".repeat(20_000),
            "x".repeat(50_000),
        )
        val start = System.nanoTime()
        inputs.forEach { IsBuildOutputParser.parseLine(it) }
        val ms = (System.nanoTime() - start) / 1_000_000
        assertTrue("parseLine took ${ms}ms — possible catastrophic backtracking", ms < 2_000)
    }
}
