package io.microconfig.plugin.run.impl

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.PlatformTestUtil
import io.microconfig.plugin.MicroconfigRepoTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicReference

class RunExecutionTest : MicroconfigRepoTestCase() {
    @Test
    fun filtersGeneratedComponentsByGroup() {
        configureGroups()

        val destination = generatedConfigs(groups = " frontend ")

        assertEquals(setOf("payments"), childDirectories(destination))
        assertTrue(File(destination, "payments/application.yaml").readText().contains("http://localhost:9761"))
    }

    @Test
    fun filtersGeneratedComponentsByService() {
        val destination = generatedConfigs(services = " payments ")

        assertEquals(setOf("payments"), childDirectories(destination))
    }

    @Test
    fun combinesGroupsAndServices() {
        configureGroups()

        val destination = generatedConfigs(groups = " backend, frontend ", services = " payments ")

        assertEquals(setOf("payments"), childDirectories(destination))
    }

    @Test
    fun acceptsCommaSeparatedServices() {
        val destination = generatedConfigs(services = " eureka, payments ")

        assertEquals(setOf("eureka", "payments"), childDirectories(destination))
    }

    @Test
    fun ignoresBlankGroupAndServiceFilters() {
        val destination = generatedConfigs(groups = " \t ", services = " \n ")

        assertEquals(setOf("eureka", "payments"), childDirectories(destination))
    }

    @ParameterizedTest
    @ValueSource(strings = ["dev, prod", "*"])
    fun buildsMultipleEnvironmentsInSeparateDirectories(envs: String) {
        val destination = generatedConfigs(env = envs)

        assertEquals(setOf("dev", "prod"), childDirectories(destination))
        assertTrue(File(destination, "dev/payments/application.yaml").readText().contains("http://localhost:8761"))
        assertTrue(File(destination, "prod/payments/application.yaml").readText().contains("http://localhost:9761"))
    }

    @Test
    fun excludesEnvironmentFromWildcardBuild() {
        val destination = generatedConfigs(env = "*, !prod")

        assertEquals(setOf("dev"), childDirectories(destination))
        assertTrue(File(destination, "dev/payments/application.yaml").isFile)
    }

    @Test
    fun supportsSpacesInRepositoryAndDestinationPaths() {
        val moved = File(projectDir, "repo with spaces")
        Files.move(repoDir.toPath(), moved.toPath())
        repoDir = moved
        LocalFileSystem.getInstance().refresh(false)

        val destination = generatedConfigs()

        assertTrue(File(destination, "payments/application.yaml").readText().contains("http://localhost:9761"))
    }

    @Test
    fun usesCustomConfigTypeAndResultFileName() {
        writeRepoFile("microconfig.yaml", """
            configTypes:
              - manifest:
                  sourceExtensions: [.manifest]
                  resultFileName: rendered-config
        """.trimIndent())
        writeRepoFile("components/payments/values.manifest", "replicas: 3\n")

        val destination = generatedConfigs(services = "payments")

        assertTrue(File(destination, "payments/rendered-config.yaml").readText().contains("replicas: 3"))
    }

    @Test
    fun returnsFailureForUnknownEnvironment() {
        assertRunFails(env = "missing")
    }

    @Test
    fun returnsFailureForUnknownGroup() {
        assertRunFails(groups = "missing")
    }

    @Test
    fun returnsFailureForUnknownService() {
        assertRunFails(services = "missing")
    }

    private fun configureGroups() {
        writeRepoFile("envs/prod.yaml", """
            backend:
              components: [eureka]
            frontend:
              components: [payments]
        """.trimIndent())
    }

    private fun generatedConfigs(env: String = "prod", groups: String = "", services: String = ""): File {
        val destination = File(projectDir, "generated configs")
        val settings = configuration(env, groups, services, destination)
        assertEquals(0, execute(settings), "Microconfig must finish successfully")
        return destination
    }

    private fun configuration(env: String, groups: String, services: String, destination: File): RunnerAndConfigurationSettings {
        val factory = ConfigurationTypeUtil.findConfigurationType(RunConfigType::class.java).configurationFactories.first()
        return RunManager.getInstance(project()).createConfiguration("Generate", factory).apply {
            (configuration as RunConfig).apply {
                this.env = env
                this.groups = groups
                this.services = services
                this.destination = destination.absolutePath
            }
        }
    }

    private fun execute(settings: RunnerAndConfigurationSettings): Int {
        val environment = ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), settings).build()
        val result = AtomicReference<RunContentDescriptor>()
        environment.setCallback { result.set(it) }
        environment.runner.execute(environment)
        PlatformTestUtil.waitWithEventsDispatching("Run didn't start", { result.get() != null }, 30)
        val descriptor = checkNotNull(result.get())
        try {
            val process = checkNotNull(descriptor.processHandler)
            assertTrue(process.waitFor(30_000), "Microconfig didn't finish")
            return checkNotNull(process.exitCode)
        } finally {
            Disposer.dispose(descriptor)
        }
    }

    private fun childDirectories(directory: File): Set<String> =
        directory.listFiles().orEmpty().filter { it.isDirectory }.map { it.name }.toSet()

    private fun assertRunFails(env: String = "prod", groups: String = "", services: String = "") {
        val destination = File(projectDir, "failed build")
        assertNotEquals(0, execute(configuration(env, groups, services, destination)))
        assertTrue(destination.walkTopDown().none { it.isFile }, "Failed selection must not generate configurations")
    }
}
