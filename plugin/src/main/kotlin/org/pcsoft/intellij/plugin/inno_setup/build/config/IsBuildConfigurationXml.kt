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

package org.pcsoft.intellij.plugin.inno_setup.build.config

import com.intellij.openapi.util.JDOMUtil
import org.jdom.Element

/**
 * Reads and writes a single [IsBuildConfiguration] as the XML stored in one `.build` directory file
 * (`<name>.build.xml`).
 *
 * Kept free of any IntelliJ project state so the format is unit-testable on its own — the same split the
 * build already uses for [org.pcsoft.intellij.plugin.inno_setup.build.IsCompilerService.buildCommandLine].
 */
object IsBuildConfigurationXml {

    /** Root tag of a build configuration file. */
    const val ROOT_TAG: String = "innoSetupBuildConfiguration"

    /** Extension (including the category part) of a build configuration file. */
    const val FILE_SUFFIX: String = ".build.xml"

    /** Characters that must not appear in a file name, replaced by `_` when deriving one from a name. */
    private val ILLEGAL_FILE_CHARS = Regex("""[\\/:*?"<>|]""")

    /** Serialises [config] into a JDOM element. */
    fun write(config: IsBuildConfiguration): Element = Element(ROOT_TAG).apply {
        setAttribute("name", config.name)
        setAttribute("compilerSymbols", config.compilerSymbols)
        setAttribute("outputDirOverride", config.outputDirOverride)
        setAttribute("additionalIsccOptions", config.additionalIsccOptions)
    }

    /**
     * Deserialises [element] into a configuration, or `null` when it carries no usable name — an empty
     * or foreign file must not silently become a nameless configuration.
     */
    fun read(element: Element): IsBuildConfiguration? {
        val name = element.getAttributeValue("name")?.trim().orEmpty()
        if (name.isEmpty()) return null
        return IsBuildConfiguration(
            name = name,
            compilerSymbols = element.getAttributeValue("compilerSymbols").orEmpty(),
            outputDirOverride = element.getAttributeValue("outputDirOverride").orEmpty(),
            additionalIsccOptions = element.getAttributeValue("additionalIsccOptions").orEmpty()
        )
    }

    /** Renders [config] as pretty-printed XML text, ready to be written to disk. */
    fun toText(config: IsBuildConfiguration): String = JDOMUtil.write(write(config))

    /** Parses XML [text] back into a configuration; returns `null` for malformed or nameless content. */
    fun fromText(text: String): IsBuildConfiguration? =
        runCatching { read(JDOMUtil.load(text)) }.getOrNull()

    /** The `.build/` file name for a configuration called [name], with unusable characters replaced. */
    fun fileNameFor(name: String): String = name.trim().replace(ILLEGAL_FILE_CHARS, "_") + FILE_SUFFIX
}
