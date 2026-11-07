package io.microconfig.plugin.run.impl

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.util.PathUtil
import com.intellij.util.SystemProperties.getJavaHome
import io.microconfig.MicroconfigMain
import io.microconfig.plugin.microconfig.MicroconfigInitializer
import java.io.File

class RunnerState(environment: ExecutionEnvironment, private val configuration: RunConfig) : CommandLineState(environment) {
    override fun startProcess(): ProcessHandler {
        val commandLine = GeneralCommandLine(createJavaCommand())
        val handler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(handler)
        return handler
    }

    private fun createJavaCommand(): List<String> = buildList {
        add(File(getJavaHome(), "bin/java").absolutePath)
        add("-XX:TieredStopAtLevel=1")
        add("-jar")
        add(PathUtil.getJarPathForClass(MicroconfigMain::class.java))
        addMicroconfigParameters(this)
    }

    private fun addMicroconfigParameters(command: MutableList<String>) {
        val projectDir = File(checkNotNull(environment.project.basePath))
        addParameter(command, ROOT, MicroconfigInitializer.create().findConfigRootDir(projectDir).absolutePath)
        val env = withoutWhitespace(configuration.env)
        addParameter(command, if ('*' in env || ',' in env) ENVS else ENV, env)
        addParameter(command, GROUPS, withoutWhitespace(configuration.groups))
        addParameter(command, SERVICES, withoutWhitespace(configuration.services))
        addParameter(command, DESTINATION, configuration.destination)
    }

    private fun addParameter(command: MutableList<String>, name: String, value: String) {
        if (value.isEmpty()) return
        command.add("-$name")
        command.add(value)
    }

    private fun withoutWhitespace(value: String): String = value.replace(Regex("\\s+"), "")

    companion object {
        const val ROOT = "r"
        const val ENV = "e"
        const val ENVS = "envs"
        const val GROUPS = "g"
        const val SERVICES = "s"
        const val DESTINATION = "d"
    }
}
