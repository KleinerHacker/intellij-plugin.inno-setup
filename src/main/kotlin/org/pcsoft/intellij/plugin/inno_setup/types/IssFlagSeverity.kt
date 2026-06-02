package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonProperty

enum class IssFlagSeverity {
    @JsonProperty("warning") WARNING,
    @JsonProperty("error") ERROR
}
