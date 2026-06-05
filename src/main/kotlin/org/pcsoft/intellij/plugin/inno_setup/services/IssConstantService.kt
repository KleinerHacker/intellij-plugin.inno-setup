package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.components.Service
import org.pcsoft.intellij.plugin.inno_setup.types.IssConstantSpec

@Service(Service.Level.APP)
class IssConstantService {
    val spec: IssConstantSpec by lazy {
        val mapper = YAMLMapper.builder()
            .addModule(kotlinModule())
            .build()
        val stream = IssConstantService::class.java
            .getResourceAsStream("/spec/iss-const.yaml")
            ?: error("iss-const.yaml not found in resources")
        mapper.readValue(stream)
    }
}
