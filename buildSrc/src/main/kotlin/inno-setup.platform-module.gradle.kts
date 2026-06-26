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

import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.time.Duration

// Convention for the language sub-modules (:language:script, :language:preprocessor): an IntelliJ
// Platform *module* (not a publishable plugin) sharing the Kotlin/Jackson/IDE/test setup. The actual
// publishable plugin (:plugin) configures org.jetbrains.intellij.platform itself.
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform.module")
}

kotlin {
    jvmToolchain(21)
}

intellijPlatform {
    instrumentCode = false
}

dependencies {
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.22.0")
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        // Single source of truth for the target IDE across all modules: a local IDE when configured
        // (Gradle property `localIdePath` or env `LOCAL_IDE_PATH`), otherwise the downloaded SDK. Pointing
        // this at a non-2025.3 IDE makes the platform tests hang during app boot.
        val localIdePath = (providers.gradleProperty("localIdePath").orNull
            ?: providers.environmentVariable("LOCAL_IDE_PATH").orNull)?.takeIf { it.isNotBlank() }
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            intellijIdea("2025.3.5")
        }
        testFramework(TestFrameworkType.Platform)
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs(
        "-Didea.log.config.file=idea/log4j.xml",
        "-Didea.log.level=OFF",
    )
    // Hard backstop so a hung test cannot stall the build indefinitely (mirrors the former root config).
    timeout.set(Duration.ofMinutes(15))
}
