package io.microconfig.plugin.greeter.impl

import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.util.JDOMUtil
import com.intellij.util.xmlb.XmlSerializer
import io.microconfig.plugin.MicroconfigRepoTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class MicroconfigSettingsTest : MicroconfigRepoTestCase() {
    @Test
    fun loadsPreviousJavaPluginApplicationSettings() {
        val xml = checkNotNull(javaClass.getResourceAsStream("/legacy/microconfig.xml")).use(JDOMUtil::load)
        val state = XmlSerializer.deserialize(xml.getChild("component"), MicroconfigSettings::class.java)
        val settings = getApplication().getService(MicroconfigSettings::class.java)

        settings.loadState(state)

        assertEquals("1.6.0", settings.version)
        assertSame(settings, settings.state)
    }

    @Test
    fun savesAndRestoresCurrentVersion() {
        val settings = MicroconfigSettings().apply { version = "1.7.0" }

        val restored = XmlSerializer.deserialize(XmlSerializer.serialize(settings.state), MicroconfigSettings::class.java)

        assertEquals("1.7.0", restored.version)
    }

    @Test
    fun defaultsToUnseenVersion() {
        assertEquals("0.0.0", MicroconfigSettings().version)
    }
}
