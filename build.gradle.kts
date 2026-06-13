/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

import com.github.jk1.license.render.ReportRenderer
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
    id("org.jetbrains.changelog") version "2.5.0"
    id("org.jetbrains.grammarkit") version "2023.3.0.3"
    id("org.jetbrains.intellij.platform")  // version managed by settings plugin
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("com.github.jk1.dependency-license-report") version "2.5"
    id("app.cash.licensee") version "1.14.1"
}

val generatedRoot = "build/parsing/gen"
val parsingRoot = "src/main/resources/parsing"

val rootPackage = "org/pcsoft/intellij/plugin/inno_setup"
val languagePackage = "$rootPackage/language"
val preprocessorPackage = "$languagePackage/parser/preprocessor"
val sectionPackage = "$languagePackage/parser/section"

intellijPlatform {
    instrumentCode = false
}

kotlin {
    jvmToolchain(21)
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    testImplementation("junit:junit:4.13.2")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.3.5")
        testFramework(TestFrameworkType.Platform)

        // Kotlin plugin needed for compilation of the optional K2-compatibility extension
        bundledPlugin("org.jetbrains.kotlin")
    }
}

kover {
    reports {
        filters {
            excludes {
                // Declarations annotated @Generated are intentionally uncoverable
                // (defensive / provably-unreachable code).
                annotatedBy("org.pcsoft.intellij.plugin.inno_setup.Generated")
            }
        }
    }
}

licenseReport {
    outputDir = layout.buildDirectory.dir("licences").get().asFile.absolutePath

    configurations = arrayOf("runtimeClasspath")

    renderers = arrayOf<ReportRenderer>(
        com.github.jk1.license.render.JsonReportRenderer(),
        com.github.jk1.license.render.SimpleHtmlReportRenderer()
    )
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    plugins.withId("app.cash.licensee") {
        extensions.configure<app.cash.licensee.LicenseeExtension> {
            listOf(
                "Apache-2.0",
            ).forEach(::allow)
        }
    }
}

tasks {
    //region Dokka
    register<Copy>("copyDokka") {
        group = "dokka"
        description = "Copy all Dokka to MkDocs"
        from(File("build/dokka"))
        into(File("docs/docs/dokka"))
        dependsOn("dokkaGeneratePublicationHtml")
    }

    register<Delete>("deleteDokka") {
        group = "dokka"
        description = "Delete Dokka"
        delete(File("docs/docs/dokka"))
    }
    //endregion

    //region Licencing
    register<Copy>("copyLicenceReport") {
        group = "licencing"
        description = "Copy licence report to MkDocs"
        from(File("build/licences"))
        into(File("docs/docs/licences"))
        dependsOn("generateLicenseReport")
    }

    register<Delete>("deleteLicenceReport") {
        group = "licencing"
        description = "Delete licence report"
        delete(File("docs/docs/licences"))
    }
    //endregion

    //region MkDocs
    register<Exec>("installMkDocs") {
        group = null
        description = "Install mkdocs"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs")
    }

    register<Exec>("installMkDocsMaterial") {
        group = null
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs-material")
    }

    register<Exec>("installGitHubPages") {
        group = null
        description = "Install ghp-import"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "ghp-import")
    }

    register<Exec>("installI18N") {
        group = null
        description = "Install i18n"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs-static-i18n")
    }

    register("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs and dependencies"
        dependsOn("installMkDocs")
        dependsOn("installMkDocsMaterial")
        dependsOn("installGitHubPages")
        dependsOn("installI18N")
    }

    register<Exec>("runDocs") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description = "Deploy mkdocs to gh-pages"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "gh-deploy", "--force")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }
    //endregion

    //region Grammar-Kit

    register<GenerateParserTask>("generateIsSectionParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsSectionGrammar.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir(generatedRoot))
        pathToParser.set("$sectionPackage/parsing/parser/IsSectionParser.java")
        pathToPsiRoot.set("$sectionPackage/parsing/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsSectionLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsSectionLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$sectionPackage/parsing"))
        purgeOldFiles.set(true)
    }

    register<GenerateParserTask>("generateIsPreprocessorParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorGrammar.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir(generatedRoot))
        pathToParser.set("$preprocessorPackage/parsing/parser/IsPreprocessorParser.java")
        pathToPsiRoot.set("$preprocessorPackage/parsing/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsPreprocessorLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$preprocessorPackage/parsing"))
        purgeOldFiles.set(true)
    }

    // Aggregator tasks: generate all lexers / all parsers as a single unit, plus an umbrella.
    register("generateLexers") {
        group = "grammar-kit"
        description = "Generate all JFlex lexers (Section + Preprocessor)"
        dependsOn("generateIsSectionLexer", "generateIsPreprocessorLexer")
    }

    register("generateParsers") {
        group = "grammar-kit"
        description = "Generate all Grammar-Kit parsers/PSI (Section + Preprocessor)"
        dependsOn("generateIsSectionParser", "generateIsPreprocessorParser")
    }

    // Wipes the whole generated tree before a full regeneration. Catches stale files left
    // behind by renamed/removed grammar packages (purgeOldFiles only cleans a task's *current*
    // output package, not folders that are no longer generated at all).
    register<Delete>("cleanGeneratedSources") {
        group = "grammar-kit"
        description = "Delete all generated lexer/parser sources (incl. stale renamed packages)"
        delete(generatedRoot)
    }

    register("generateSources") {
        group = "grammar-kit"
        description = "Generate all lexers and parsers (from a clean slate)"
        dependsOn("cleanGeneratedSources", "generateLexers", "generateParsers")
    }

    // When a full regeneration is requested, every generator must run *after* the wipe.
    // Routine compiles depend on the generators directly (see below), so the wipe is not in
    // their graph and incremental up-to-date checks keep working.
    listOf(
        "generateIsSectionParser",
        "generateIsSectionLexer",
        "generateIsPreprocessorParser",
        "generateIsPreprocessorLexer"
    ).forEach { taskName ->
        named(taskName) { mustRunAfter("cleanGeneratedSources") }
    }

    sourceSets.main {
        java.srcDir(generatedRoot)
    }

    compileJava {
        dependsOn("generateLexers", "generateParsers")
    }

    compileKotlin {
        dependsOn("generateLexers", "generateParsers")
    }
//endregion
}
