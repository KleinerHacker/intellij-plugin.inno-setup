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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SimpleModificationTracker

/**
 * Counts the changes of the symbols contributed through [IsPreprocessorSymbolProvider], so caches that seed
 * themselves from those symbols can depend on something that actually moves when they change.
 *
 * The branch analysis is cached per document revision, which is the right dependency for everything declared
 * *inside* the script. The `/D…` symbols of a build configuration live entirely outside it: selecting another
 * run configuration or editing a build configuration changes the analysis result without touching a single
 * character, and a mere daemon restart would then re-read the unchanged — and now wrong — cache entry.
 *
 * Every place that can change an external symbol source calls [symbolsChanged]; the analysis lists this
 * tracker as a second cache dependency.
 */
@Service(Service.Level.PROJECT)
class IsPreprocessorSymbolTracker : SimpleModificationTracker() {

    /** Marks the externally contributed symbols as changed, invalidating every cache depending on them. */
    fun symbolsChanged(): Unit = incModificationCount()

    companion object {

        /** Returns the symbol tracker of [project]. */
        fun getInstance(project: Project): IsPreprocessorSymbolTracker =
            project.getService(IsPreprocessorSymbolTracker::class.java)
    }
}
