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

// :language:preprocessor — the ISPP preprocessor language (lexer/parser/PSI, highlighter, basic annotator,
// brace matcher, references, expression engine). The lowest layer: it depends on no other plugin module and
// is pulled in by :language:script. Carries its own plugin.xml fragment (META-INF/inno-setup-preprocessor.xml).
plugins {
    id("inno-setup.platform-module")
    id("org.jetbrains.grammarkit") version "2023.3.0.4"
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
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
val preprocessorPackage = "org/pcsoft/intellij/plugin/inno_setup/preprocessor/language/parser"

tasks {
    register<GenerateParserTask>("generateIsPreprocessorParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorGrammar.bnf"))
        targetRootOutputDir.set(layout.buildDirectory.dir("generated"))
        pathToParser.set("$preprocessorPackage/IsPreprocessorParser.java")
        pathToPsiRoot.set("$preprocessorPackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsPreprocessorLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorLexer.flex"))
        targetOutputDir.set(layout.buildDirectory.dir("generated/$preprocessorPackage"))
        // NOTE: purgeOldFiles MUST stay false. The lexer shares its output directory
        // (…/parser) with the parser task, whose PSI classes live in the …/parser/psi
        // subdirectory. A purging lexer deletes that subdirectory recursively, and since
        // there is no ordering between the two generate tasks the parser's PSI output is
        // wiped whenever the lexer happens to run last (order differs between machines/CI),
        // producing "Unresolved reference" compile errors for the generated PSI types.
        purgeOldFiles.set(false)
    }

    compileJava {
        dependsOn("generateIsPreprocessorLexer", "generateIsPreprocessorParser")
    }

    compileKotlin {
        dependsOn("generateIsPreprocessorLexer", "generateIsPreprocessorParser")
    }
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated"))
}
