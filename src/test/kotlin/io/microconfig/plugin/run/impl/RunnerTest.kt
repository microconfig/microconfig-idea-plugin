package io.microconfig.plugin.run.impl

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor.EXECUTOR_ID
import io.microconfig.plugin.MicroconfigRepoTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class RunnerTest : MicroconfigRepoTestCase() {
    @Test
    fun acceptsOnlyRunExecutorWithMicroconfigConfiguration() {
        val runner = Runner()
        val type = RunConfigType()
        val config = RunConfigFactory(type).createTemplateConfiguration(project())

        assertTrue(runner.canRun(EXECUTOR_ID, config))
        assertFalse(runner.canRun("Debug", config))
        assertFalse(runner.canRun(EXECUTOR_ID, mock(RunProfile::class.java)))
    }

    @Test
    fun retainsRunTypeFactoryAndRunnerIdentifiers() {
        val type = RunConfigType()

        assertEquals("RunConfigType", type.id)
        assertEquals("Microconfig", type.displayName)
        assertEquals("Microconfig", type.configurationFactories.single().id)
        assertEquals("Runner", Runner().runnerId)
    }
}
