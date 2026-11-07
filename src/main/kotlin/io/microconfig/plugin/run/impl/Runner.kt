package io.microconfig.plugin.run.impl

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor.EXECUTOR_ID
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.executeState
import com.intellij.execution.ui.RunContentDescriptor

class Runner : GenericProgramRunner<RunnerSettings>() {
    override fun getRunnerId(): String = "Runner"

    override fun canRun(executorId: String, profile: RunProfile): Boolean = executorId == EXECUTOR_ID && profile is RunConfig

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? =
        executeState(state, environment, this)
}
