package io.microconfig.plugin.run.impl

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

class RunConfigFactory(type: RunConfigType) : ConfigurationFactory(type) {
    override fun getId(): String = "Microconfig"

    override fun createTemplateConfiguration(project: Project): RunConfiguration = RunConfig(this, project)
}
