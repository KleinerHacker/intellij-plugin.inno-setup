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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.preprocessor

import junit.framework.TestCase
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorHost
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorHostLine
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.IsLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.template.IsTemplateFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.impl.IsSectionPreprocessorLineMixinImpl
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.template.psi.impl.IsTemplatePreprocessorLineMixinImpl

/**
 * Structural guard for the module-boundary host abstraction introduced by the multi-module split: the ISPP
 * preprocessor module ([:language:preprocessor]) talks to the script/template host only through the
 * [IsPreprocessorHost] / [IsPreprocessorHostLine] interfaces. These assertions fail fast if a refactor
 * accidentally drops the interface from a host file or host preprocessor line — which would silently break
 * `#define` collection, `{#Name}` usage detection and `#include` problem surfacing across the modules.
 */
class IsPreprocessorHostWiringTest : TestCase() {

    fun testHostFilesImplementIsPreprocessorHost() {
        assertTrue(
            ".iss script must be an ISPP host",
            IsPreprocessorHost::class.java.isAssignableFrom(IsScriptFile::class.java)
        )
        assertTrue(
            ".isl language file must be an ISPP host",
            IsPreprocessorHost::class.java.isAssignableFrom(IsLanguageFile::class.java)
        )
        assertTrue(
            ".ist template must be an ISPP host",
            IsPreprocessorHost::class.java.isAssignableFrom(IsTemplateFile::class.java)
        )
    }

    fun testHostPreprocessorLinesImplementIsPreprocessorHostLine() {
        assertTrue(
            "section preprocessor line must be an ISPP host line",
            IsPreprocessorHostLine::class.java.isAssignableFrom(IsSectionPreprocessorLineMixinImpl::class.java)
        )
        assertTrue(
            "template preprocessor line must be an ISPP host line",
            IsPreprocessorHostLine::class.java.isAssignableFrom(IsTemplatePreprocessorLineMixinImpl::class.java)
        )
    }
}
