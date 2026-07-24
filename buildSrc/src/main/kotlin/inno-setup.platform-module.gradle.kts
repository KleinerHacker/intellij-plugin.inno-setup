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

import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.Duration

// Target IDE version — single source of truth in gradle/libs.versions.toml (registered as the "libs"
// catalog via buildSrc/settings.gradle.kts).
val ideaVersion = extensions.getByType<VersionCatalogsExtension>()
    .named("libs").findVersion("idea").get().requiredVersion

// The IntelliJ Platform ships its own kotlin-stdlib as an IDE jar (not a resolved Gradle module), so it
// does NOT participate in dependency conflict resolution. Transitive dependencies (jackson-module-kotlin
// pulls kotlin-reflect → kotlin-stdlib) therefore decide the version on the test/runtime classpath. If
// that resolves to a stdlib older than the compiler (2.2.20+ emits @DebugMetadata version 2), the
// platform's coroutine debug probes crash with "Debug metadata version mismatch. Expected: 1, got 2",
// which kills the plugin-descriptor-loading workers and hangs every platform test until the Gradle
// timeout. Pin the Kotlin artifacts to the catalog version (matching both the compiler and the bundled
// 2.4.0 stdlib) so the running stdlib understands the metadata the compiler emits.
val kotlinVersion = extensions.getByType<VersionCatalogsExtension>()
    .named("libs").findVersion("kotlin").get().requiredVersion

// Convention for the language sub-modules (:language:script, :language:preprocessor): an IntelliJ
// Platform *module* (not a publishable plugin) sharing the Kotlin/Jackson/IDE/test setup. The actual
// publishable plugin (:plugin) configures org.jetbrains.intellij.platform itself.
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform.module")
    `maven-publish`
}

kotlin {
    // Compile with JDK 25: since IntelliJ 2026.2 the platform jars are Java 25 (class file 69), so an
    // older javac/kotlinc cannot even read them. The emitted bytecode is pinned to Java 21 below so the
    // plugin still loads on the whole supported IDE range (sinceBuild 261).
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

// Publish the language modules (:language:script, :language:preprocessor) as regular Maven artifacts to
// the GitHub Packages registry. Wired into the release workflow after all other release steps but before
// the GitHub release page is created. Credentials come from the CI environment (GITHUB_ACTOR/GITHUB_TOKEN).
publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            artifactId = "inno-setup-${project.name}"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            // GitHub Packages requires the URL path to match the actual owner/repo; GITHUB_REPOSITORY is
            // set by CI ("owner/repo"), otherwise fall back to the known repository name.
            val repository = System.getenv("GITHUB_REPOSITORY") ?: "KleinerHacker/intellij-plugin.inno-setup"
            url = uri("https://maven.pkg.github.com/$repository")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

intellijPlatform {
    instrumentCode = false
}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    }

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.22.0")
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        // Single source of truth for the target IDE across all modules: a local IDE when configured
        // (Gradle property `localIdePath` or env `LOCAL_IDE_PATH`), otherwise the downloaded SDK. Pointing
        // this at an IDE whose build differs from the target version makes the platform tests hang during app boot.
        val localIdePath = (providers.gradleProperty("localIdePath").orNull
            ?: providers.environmentVariable("LOCAL_IDE_PATH").orNull)?.takeIf { it.isNotBlank() }
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            intellijIdea(ideaVersion)
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
