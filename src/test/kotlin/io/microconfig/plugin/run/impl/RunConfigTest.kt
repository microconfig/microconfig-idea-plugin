package io.microconfig.plugin.run.impl

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunConfigurable
import com.intellij.execution.impl.SingleConfigurationConfigurable
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.util.ui.UIUtil
import io.microconfig.plugin.MicroconfigRepoTestCase
import org.jdom.Element
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JLabel
import javax.swing.JTextField

class RunConfigTest : MicroconfigRepoTestCase() {
    @Test
    fun defaultsToMcName() {
        val config = factory().createTemplateConfiguration(project()) as RunConfig
        assertEquals("mc", config.name)
        assertEquals("mc", config.suggestedName())
    }

    @Test
    fun preservesExplicitName() {
        val settings = RunManager.getInstance(project()).createConfiguration("production configs", factory())
        assertEquals("production configs", settings.configuration.name)
    }

    @Test
    fun preservesUniqueNameWhenEnvironmentChanges() {
        val configurable = RunConfigurable(project())
        try {
            configurable.createComponent()
            configurable.reset()
            val first = configurable.createNewConfiguration(factory())
            val second = configurable.createNewConfiguration(factory())
            assertEquals("mc", first.nameText)
            assertEquals("mc (1)", second.nameText)

            changeEnvironment(second, "prod")

            assertEquals("mc (1)", second.nameText)
            assertEquals("prod", (second.editor.snapshot.configuration as RunConfig).env)
        } finally {
            Disposer.dispose(configurable)
        }
    }

    @Test
    fun settingsSurviveSerialization() {
        val config = newRunConfig()
        val element = Element("configuration")
        config.writeExternal(element)
        val restored = factory().createTemplateConfiguration(project()) as RunConfig
        restored.readExternal(element)
        assertSameSettings(config, restored)
    }

    @Test
    fun editorAppliesSettingsToClone() {
        val config = newRunConfig()
        val clone = config.clone() as RunConfig
        val editor = config.configurationEditor
        try {
            editor.component
            editor.resetFrom(config)
            clone.env = "other"
            editor.applyTo(clone)
            assertSameSettings(config, clone)
            assertNotSame(editor, clone.configurationEditor)
        } finally {
            Disposer.dispose(editor)
        }
    }

    @Test
    fun runGeneratesConfigs() {
        val destination = File(projectDir, "build/configs with spaces")
        val settings = RunManager.getInstance(project()).createConfiguration("Generate", factory())
        val config = settings.configuration as RunConfig
        config.env = "prod"
        config.destination = destination.absolutePath
        val descriptor = run(settings)
        try {
            val process = checkNotNull(descriptor.processHandler)
            assertTrue(process.waitFor(120_000), "microconfig didn't finish")
            assertEquals(0, process.exitCode)
            val payments = Files.readString(File(destination, "payments/application.yaml").toPath())
            assertTrue(payments.contains("http://localhost:9761"), payments)
        } finally {
            Disposer.dispose(descriptor)
        }
    }

    private fun factory(): ConfigurationFactory =
        ConfigurationTypeUtil.findConfigurationType(RunConfigType::class.java).configurationFactories.first()

    private fun changeEnvironment(configurable: SingleConfigurationConfigurable<*>, env: String) {
        val label = UIUtil.findComponentsOfType(configurable.editor.component, JLabel::class.java)
            .first { it.text == "Environment: " }
        (label.labelFor as JTextField).text = env
        PlatformTestUtil.dispatchAllEventsInIdeEventQueue()
    }

    private fun newRunConfig(): RunConfig = (factory().createTemplateConfiguration(project()) as RunConfig).apply {
        env = "prod"
        groups = "main"
        services = "payments"
        destination = "/tmp/configs"
    }

    private fun assertSameSettings(expected: RunConfig, actual: RunConfig) {
        assertEquals(expected.env, actual.env)
        assertEquals(expected.groups, actual.groups)
        assertEquals(expected.services, actual.services)
        assertEquals(expected.destination, actual.destination)
    }

    private fun run(settings: RunnerAndConfigurationSettings): RunContentDescriptor {
        val environment = ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), settings).build()
        assertInstanceOf(Runner::class.java, environment.runner)
        val descriptor = AtomicReference<RunContentDescriptor>()
        environment.callback = { descriptor.set(it) }
        environment.runner.execute(environment)
        PlatformTestUtil.waitWithEventsDispatching("run didn't start", { descriptor.get() != null }, 60)
        return checkNotNull(descriptor.get())
    }
}
