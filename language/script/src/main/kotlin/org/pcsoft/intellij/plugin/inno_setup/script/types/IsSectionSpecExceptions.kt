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

package org.pcsoft.intellij.plugin.inno_setup.script.types

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsSectionSpecTarget
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.appliesTo

/**
 * Cases in which the constraint of the enclosing spec block does not apply. Each listed case lifts the
 * constraint on its own; the block is written directly inside the constraint it inverts — `mustExists`
 * for the compile-time existence of a path, `required` for a mandatory attribute.
 *
 * @property byFlag Flags of the same section entry that lift the constraint. Under
 *                  [IsSectionMustExists] the path is then only checked for invalid path characters
 *                  (`\[Files]` `Source` with `external`); under [IsSectionRequiredSpec] the attribute
 *                  may be omitted for every target in [IsSectionRequiredSpec.files] (`\[Files]`
 *                  `DestDir` with `dontcopy`).
 * @property byAttribute Other attributes of the same section that lift the constraint by taking its
 *                       place. Only meaningful under [IsSectionRequiredSpec]: Inno Setup accepts
 *                       either/or pairs — `AppVersion` may be replaced by `AppVerName` — so the
 *                       attribute must not be reported as missing while one of these is present.
 */
data class IsSectionSpecExceptions(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    @JsonProperty("by-flag")
    val byFlag: Set<String> = emptySet(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    @JsonProperty("by-attribute")
    val byAttribute: Set<String> = emptySet(),
)

/**
 * Marks a `file`/`directory` typed value as having to exist on the build machine at compile time.
 *
 * The mere presence of the `mustExists` key enables the check — in the spec YAML the usual form is the
 * bare `mustExists:` with no value. An absent key means the value is a target/runtime path that is only
 * validated for invalid path characters.
 *
 * @property except Cases in which the existence check is skipped, leaving only the invalid-character
 *                  check.
 */
@JsonDeserialize(using = IsSectionMustExistsDeserializer::class)
data class IsSectionMustExists(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val except: IsSectionSpecExceptions? = null,
)

/**
 * Deserializer that makes an explicitly written but valueless `mustExists:` distinguishable from an
 * absent `mustExists` key.
 *
 * Jackson never invokes a deserializer for a key that is not present at all, so the Kotlin default of
 * the property stays `null` in that case. An explicit `mustExists:` produces a `null` token, which
 * Jackson routes to [getNullValue] — returning an instance there turns the bare key into "the path must
 * exist".
 */
class IsSectionMustExistsDeserializer : JsonDeserializer<IsSectionMustExists>() {
    /**
     * Reads the object form `mustExists:` / `except:` / `by-flag: [...]`.
     */
    override fun deserialize(parser: JsonParser, context: DeserializationContext): IsSectionMustExists {
        val node = parser.codec.readTree<JsonNode>(parser)
        if (node.isNull) return IsSectionMustExists()
        val except = (node as? ObjectNode)?.get("except")
            ?.let { parser.codec.treeToValue(it, IsSectionSpecExceptions::class.java) }
        return IsSectionMustExists(except)
    }

    /**
     * Maps the valueless `mustExists:` (a `null` token) to an instance without exceptions.
     */
    override fun getNullValue(context: DeserializationContext): IsSectionMustExists = IsSectionMustExists()
}

/**
 * Marks an attribute as mandatory within its section. An absent `required` key means the attribute is
 * optional.
 *
 * @property files File types in which the attribute must be specified within its section.
 * @property except Cases in which the attribute may be omitted although it is listed as mandatory.
 */
data class IsSectionRequiredSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val files: Set<IsSectionSpecTarget> = emptySet(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val except: IsSectionSpecExceptions? = null,
)

/**
 * Whether an attribute is mandatory for [target]. An absent `required` block (`null`) means the
 * attribute is optional for every target.
 */
fun IsSectionRequiredSpec?.appliesTo(target: IsSectionSpecTarget): Boolean =
    this != null && files.appliesTo(target)
