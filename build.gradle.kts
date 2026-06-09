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
val isppLanguagePackage = "$languagePackage/ispp"
val isiLanguagePackage = "$languagePackage/isi"

intellijPlatform {
    instrumentCode = false
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
        commandLine("python", "-m", "pip", "install", "mkdocs")
    }

    register<Exec>("installMkDocsMaterial") {
        group = null
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "mkdocs-material")
    }

    register<Exec>("installGitHubPages") {
        group = null
        description = "Install ghp-import"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "ghp-import")
    }

    register("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs and dependencies"
        dependsOn("installMkDocs")
        dependsOn("installMkDocsMaterial")
        dependsOn("installGitHubPages")
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

    register<GenerateParserTask>("generateIsiParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsiGrammar.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir(generatedRoot))
        pathToParser.set("$isiLanguagePackage/parsing/parser/IsiParser.java")
        pathToPsiRoot.set("$isiLanguagePackage/parsing/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsiLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsiLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$isiLanguagePackage/parsing"))
        purgeOldFiles.set(true)
    }

    register<GenerateParserTask>("generateIsppParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsppGrammar.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir(generatedRoot))
        pathToParser.set("$isppLanguagePackage/parsing/parser/IsppParser.java")
        pathToPsiRoot.set("$isppLanguagePackage/parsing/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsppLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsppLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$isppLanguagePackage/parsing"))
        purgeOldFiles.set(true)
    }

    // Aggregator tasks: generate all lexers / all parsers as a single unit, plus an umbrella.
    register("generateLexers") {
        group = "grammar-kit"
        description = "Generate all JFlex lexers (ISI + ISPP)"
        dependsOn("generateIsiLexer", "generateIsppLexer")
    }

    register("generateParsers") {
        group = "grammar-kit"
        description = "Generate all Grammar-Kit parsers/PSI (ISI + ISPP)"
        dependsOn("generateIsiParser", "generateIsppParser")
    }

    register("generateSources") {
        group = "grammar-kit"
        description = "Generate all lexers and parsers"
        dependsOn("generateLexers", "generateParsers")
    }

    sourceSets.main {
        java.srcDir(generatedRoot)
    }

    compileJava {
        dependsOn("generateSources")
    }

    compileKotlin {
        dependsOn("generateSources")
    }
//endregion
}
