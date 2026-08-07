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
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Base type for the value model of a section attribute.
 *
 * The concrete subtype is selected from the `kind` property in the bundled section specification.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes(
    JsonSubTypes.Type(value = IsSectionNativeTypeSpec::class, name = "native"),
    JsonSubTypes.Type(value = IsSectionReferenceTypeSpec::class, name = "reference"),
    JsonSubTypes.Type(value = IsSectionFlagTypeSpec::class, name = "flags"),
    JsonSubTypes.Type(value = IsSectionFileTypeSpec::class, name = "file"),
    JsonSubTypes.Type(value = IsSectionDirectoryTypeSpec::class, name = "directory"),
)
/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
sealed class IsSectionAttributeTypeSpec

/**
 * Concrete primitive data type of a native attribute value, mirroring the `dataType` enum in
 * `is-spec.json`.
 *
 * @property typeName The lowercase wire name used in the spec YAML and shown in documentation/completion.
 */
enum class IsSectionNativeDataType(val typeName: String) {
    /** Free-form text. */
    @JsonProperty("string")
    STRING("string"),

    /** A `yes`/`no` boolean. */
    @JsonProperty("boolean")
    BOOLEAN("boolean"),

    /** A decimal or Pascal-style hexadecimal (`$XXXX`) integer. */
    @JsonProperty("integer")
    INTEGER("integer"),
}

/**
 * Native scalar value type such as `string`, `boolean`, or `integer`.
 *
 * @property dataType The native primitive type used by validators and completion providers.
 */
data class IsSectionNativeTypeSpec(val dataType: IsSectionNativeDataType) : IsSectionAttributeTypeSpec()

/**
 * Value type that references a named entry in another section.
 *
 * @property section Target section whose `Name` declarations can be referenced.
 */
data class IsSectionReferenceTypeSpec(val section: String) : IsSectionAttributeTypeSpec()

/**
 * Value type consisting of whitespace-separated flags.
 *
 * @property flags Allowed flags and their validation metadata.
 */
data class IsSectionFlagTypeSpec(val flags: List<IsSectionFlagSpec>) : IsSectionAttributeTypeSpec()

/**
 * Value type denoting a path to a file. With [mustExists] the resolved path is checked to exist and to
 * be a regular file (wildcards, comma-separated lists, and unresolvable `{…}` placeholders are
 * skipped); without it only invalid path characters are reported.
 *
 * @property mustExists Set when the file must exist on the build machine at compile time, `null` for a
 *                      target/runtime path. See [IsSectionMustExists] for how a bare `mustExists:` in
 *                      the spec YAML is distinguished from an absent key.
 */
data class IsSectionFileTypeSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val mustExists: IsSectionMustExists? = null,
) : IsSectionAttributeTypeSpec()

/**
 * Value type denoting a path to a directory. With [mustExists] the resolved path is checked to exist
 * and to be a directory (wildcards, comma-separated lists, and unresolvable `{…}` placeholders are
 * skipped); without it only invalid path characters are reported.
 *
 * @property mustExists Set when the directory must exist on the build machine at compile time, `null`
 *                      for a target/runtime path.
 */
data class IsSectionDirectoryTypeSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val mustExists: IsSectionMustExists? = null,
) : IsSectionAttributeTypeSpec()
