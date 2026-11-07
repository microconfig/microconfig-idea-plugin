package io.microconfig.plugin.run.impl

import com.intellij.execution.Executor
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.jdom.Element
import java.io.File

class RunConfig(factory: RunConfigFactory, project: Project) :
    LocatableConfigurationBase<RunnerState>(project, factory, "mc"), RunnerSettings {
    var env = "dev"
    var groups = ""
    var services = ""
    var destination = File(project.basePath, "build/configs").absolutePath

    override fun getConfigurationEditor(): SettingsEditor<RunConfig> = RunConfigEditor()

    override fun suggestedName(): String = "mc"

    // Suggest the initial name without regenerating IDEA's unique name when settings change.
    override fun isGeneratedName(): Boolean = false

    override fun checkConfiguration() = Unit

    override fun writeExternal(element: Element) {
        element.addContent(settingsElement())
        super.writeExternal(element)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        val settings = element.getChild(SETTINGS_ELEMENT) ?: return
        restoreSettings(settings)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState = RunnerState(environment, this)

    private fun settingsElement(): Element = Element(SETTINGS_ELEMENT).apply {
        setAttribute(RunnerState.ENV, env)
        setAttribute(RunnerState.GROUPS, groups)
        setAttribute(RunnerState.SERVICES, services)
        setAttribute(RunnerState.DESTINATION, destination)
    }

    private fun restoreSettings(element: Element) {
        env = element.getAttributeValue(RunnerState.ENV).orEmpty()
        groups = element.getAttributeValue(RunnerState.GROUPS).orEmpty()
        services = element.getAttributeValue(RunnerState.SERVICES).orEmpty()
        destination = element.getAttributeValue(RunnerState.DESTINATION).orEmpty()
    }

    companion object {
        private const val SETTINGS_ELEMENT = "MicroconfigRunConfig"
    }
}
