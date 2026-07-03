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

import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

rootProject.name = "inno-setup"

include(":language:preprocessor")
include(":language:script")
include(":plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

// Jackson alignment on the SETTINGS plugin classpath — the top-most (parent-first) classloader of the whole
// build. The IntelliJ Platform settings plugin applied below pulls jackson-databind 2.20.x here, a version
// where the `_anyGetterWriter` field was removed. Because this classloader is the parent of every module's
// plugin classloader, that databind wins for all plugins — including CycloneDX, whose jackson-dataformat-xml
// still references the removed field ⇒ `NoSuchFieldError` in :plugin:cyclonedxBom. Pin the whole jackson
// family to the last release that still carries the field so databind and dataformat-xml stay compatible.
buildscript {
    configurations.classpath {
        resolutionStrategy.eachDependency {
            if (requested.group.startsWith("com.fasterxml.jackson")) {
                useVersion("2.18.6")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.intellij.platform.settings") version "2.17.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        gradlePluginPortal()

        // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
        intellijPlatform {
            defaultRepositories()
        }
    }
}
