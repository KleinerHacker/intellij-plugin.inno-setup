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

import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask

// :language:script — the Inno Setup language (section/INI grammar shared by .iss/.isl/.ist, file types,
// highlighter, folding, brace matching, basic annotator + quickfixes, references, include infrastructure,
// the ISPP injector). Depends on :language:preprocessor and is pulled in by :plugin. Carries its own
// plugin.xml fragment (META-INF/inno-setup-script.xml).
plugins {
    id("inno-setup.platform-module")
    id("org.jetbrains.grammarkit") version "2023.3.0.3"
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
}

dependencies {
    implementation(project(":language:preprocessor"))
}

// Coverage is measured from `test`, which runs everything. Without this Kover's report tasks — and through
// `koverVerify` the whole `check`/`build` lifecycle — would additionally pull in developerTest and
// integrationTest, running every test three times concurrently. See inno-setup.platform-module.gradle.kts.
kover {
    currentProject {
        instrumentation {
            disabledForTestTasks.addAll("developerTest", "integrationTest")
        }
    }
}

val parsingRoot = "src/main/resources/parsing"
val languagePackage = "org/pcsoft/intellij/plugin/inno_setup/script/language"
val sectionPackage = "$languagePackage/parser/section"
val templatePackage = "$languagePackage/parser/template"

tasks {
    register<GenerateParserTask>("generateIsSectionParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsSectionGrammar.bnf"))
        targetRootOutputDir.set(layout.buildDirectory.dir("generated"))
        pathToParser.set("$sectionPackage/IsSectionParser.java")
        pathToPsiRoot.set("$sectionPackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsSectionLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsSectionLexer.flex"))
        targetOutputDir.set(layout.buildDirectory.dir("generated/$sectionPackage"))
        // See preprocessor build: purgeOldFiles MUST stay false — the lexer shares its output
        // directory with the parser task, whose PSI classes live in the …/psi subdirectory. A
        // purging lexer wipes that subdirectory recursively; with no ordering between the two
        // generate tasks this deletes the parser's PSI output whenever the lexer runs last.
        purgeOldFiles.set(false)
    }

    register<GenerateParserTask>("generateIsTemplateParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsTemplateGrammar.bnf"))
        targetRootOutputDir.set(layout.buildDirectory.dir("generated"))
        pathToParser.set("$templatePackage/IsTemplateParser.java")
        pathToPsiRoot.set("$templatePackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsTemplateLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsTemplateLexer.flex"))
        targetOutputDir.set(layout.buildDirectory.dir("generated/$templatePackage"))
        // See above: purgeOldFiles MUST stay false to avoid wiping the parser's …/psi output.
        purgeOldFiles.set(false)
    }

    val generators = listOf(
        "generateIsSectionLexer", "generateIsSectionParser",
        "generateIsTemplateLexer", "generateIsTemplateParser",
    )
    compileJava { dependsOn(generators) }
    compileKotlin { dependsOn(generators) }
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated"))
}
