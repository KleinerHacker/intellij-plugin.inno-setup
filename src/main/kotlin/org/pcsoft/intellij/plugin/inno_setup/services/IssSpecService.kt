package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.components.Service
import org.pcsoft.intellij.plugin.inno_setup.types.InnoSetupSpec

@Service(Service.Level.APP)
class IssSpecService {
    val spec: InnoSetupSpec by lazy {
        val mapper = YAMLMapper.builder()
            .addModule(kotlinModule())
            .build()
        val stream = IssSpecService::class.java
            .getResourceAsStream("/spec/iss-spec.yaml")
            ?: error("iss-spec.yaml not found in resources")
        mapper.readValue(stream)
    }
}