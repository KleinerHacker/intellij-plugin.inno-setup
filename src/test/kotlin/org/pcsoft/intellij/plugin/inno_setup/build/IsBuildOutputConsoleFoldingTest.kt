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

import com.intellij.openapi.project.Project
import org.junit.Assert.*
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Tests [IsBuildOutputConsoleFolding]: indented ISCC detail lines fold, section/top-level lines do not,
 * and the placeholder reports the number of hidden lines.
 *
 * The folding never touches the [Project], so a no-op proxy is enough — this keeps the test a plain
 * JUnit test without the heavy platform fixture.
 */
class IsBuildOutputConsoleFoldingTest {

    private val folding = IsBuildOutputConsoleFolding()
    private val project = Proxy.newProxyInstance(
        Project::class.java.classLoader, arrayOf(Project::class.java)
    ) { _, _, _ -> null } as Project

    @Test
    fun `folds indented detail lines`() {
        assertTrue(folding.shouldFoldLine(project, "   Reading file: C:\\x\\Default.isl"))
        assertTrue(folding.shouldFoldLine(project, "\tMessages in script file"))
    }

    @Test
    fun `does not fold section or top-level lines`() {
        assertFalse(folding.shouldFoldLine(project, "Parsing [Setup] section, line 10"))
        assertFalse(folding.shouldFoldLine(project, "Reading file (WizardImageFile)"))
        assertFalse(folding.shouldFoldLine(project, ""))
        assertFalse(folding.shouldFoldLine(project, "    "))
    }

    @Test
    fun `placeholder reports hidden line count`() {
        assertTrue(folding.getPlaceholderText(project, listOf("a", "b", "c")).contains("3"))
        assertTrue(folding.getPlaceholderText(project, listOf("a")).contains("1 Zeile"))
    }
}
