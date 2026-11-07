package io.microconfig.plugin.run.impl

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.openapi.util.JDOMUtil
import io.microconfig.plugin.MicroconfigRepoTestCase
import org.jdom.Element
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RunConfigPersistenceTest : MicroconfigRepoTestCase() {
    @Test
    fun loadsPreviousJavaPluginRunConfiguration() {
        val settings = RunManager.getInstance(project()).createConfiguration("temporary", factory()) as RunnerAndConfigurationSettingsImpl
        val xml = checkNotNull(javaClass.getResourceAsStream("/legacy/run-configuration.xml")).use(JDOMUtil::load)

        settings.readExternal(xml, false)

        assertEquals("Production configs", settings.name)
        assertEquals("RunConfigType", settings.type.id)
        val config = settings.configuration as RunConfig
        assertEquals("prod", config.env)
        assertEquals("main", config.groups)
        assertEquals("payments", config.services)
        assertEquals("/tmp/configs with spaces", config.destination)
    }

    @Test
    fun readsMissingLegacyAttributesAsEmptyStrings() {
        val config = factory().createTemplateConfiguration(project()) as RunConfig

        config.readExternal(Element("configuration").addContent(Element("MicroconfigRunConfig")))

        assertEquals("", config.env)
        assertEquals("", config.groups)
        assertEquals("", config.services)
        assertEquals("", config.destination)
    }

    @Test
    fun retainsDefaultsWhenLegacySettingsElementIsAbsent() {
        val config = factory().createTemplateConfiguration(project()) as RunConfig

        config.readExternal(Element("configuration"))

        assertEquals("dev", config.env)
        assertEquals("", config.groups)
        assertEquals("", config.services)
        assertEquals(java.io.File(projectDir, "build/configs").absolutePath, config.destination)
    }

    @Test
    fun persistsCustomNameAndAllSettingsThroughIdeSerialization() {
        val manager = RunManager.getInstance(project())
        val original = manager.createConfiguration("My production build", factory()) as RunnerAndConfigurationSettingsImpl
        (original.configuration as RunConfig).apply {
            env = "dev,prod"
            groups = "main,secondary"
            services = "eureka,payments"
            destination = "/tmp/my configs"
        }
        val xml = Element("configuration")
        original.writeExternal(xml)
        val restored = manager.createConfiguration("temporary", factory()) as RunnerAndConfigurationSettingsImpl

        restored.readExternal(xml, false)

        assertEquals("My production build", restored.name)
        val config = restored.configuration as RunConfig
        assertEquals("dev,prod", config.env)
        assertEquals("main,secondary", config.groups)
        assertEquals("eureka,payments", config.services)
        assertEquals("/tmp/my configs", config.destination)
    }

    private fun factory() = ConfigurationTypeUtil.findConfigurationType(RunConfigType::class.java).configurationFactories.first()
}
