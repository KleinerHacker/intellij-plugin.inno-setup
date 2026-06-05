package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.components.Service
import org.pcsoft.intellij.plugin.inno_setup.types.IssIsppSpec

@Service(Service.Level.APP)
class IssIsppService {
    val spec: IssIsppSpec by lazy {
        val mapper = YAMLMapper.builder()
            .addModule(kotlinModule())
            .build()
        val stream = IssIsppService::class.java
            .getResourceAsStream("/spec/iss-ispp.yaml")
            ?: error("iss-ispp.yaml not found in resources")
        mapper.readValue(stream)
    }
}
