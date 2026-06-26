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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.template

import com.intellij.ide.structureView.StructureViewBuilder
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * A `.ist` template must have no structure view: no `psiStructureViewFactory` is registered for IST, so
 * the platform must not produce a structure-view builder for it.
 */
class IsTemplateNoStructureViewTest : IsTimedBasePlatformTestCase() {

    fun testNoStructureViewBuilder() {
        val file = myFixture.configureByText(IsTemplateFileType.INSTANCE, "[Setup]\nfoo\n")
        val builder: StructureViewBuilder? =
            com.intellij.lang.LanguageStructureViewBuilder.INSTANCE
                .getStructureViewBuilder(file)
        assertNull("IST must not provide a structure view builder", builder)
    }
}
