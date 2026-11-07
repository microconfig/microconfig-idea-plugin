package io.microconfig.plugin.run.impl

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.util.IconLoader

class RunConfigType : ConfigurationTypeBase(
    "RunConfigType", "Microconfig", "Generate Config", IconLoader.getIcon("/logo.png", RunConfigType::class.java),
) {
    init {
        addFactory(RunConfigFactory(this))
    }
}
