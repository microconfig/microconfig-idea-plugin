package io.microconfig.plugin.microconfig

import com.intellij.openapi.fileTypes.FileTypeManager
import io.microconfig.core.properties.ConfigFormat
import io.microconfig.plugin.MicroconfigRepoTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConfigOutputTest : MicroconfigRepoTestCase() {
    @Test
    fun normalizesWindowsLineEndingsWithoutChangingContent() {
        assertEquals("first\nsecond\n\n", ConfigOutput(ConfigFormat.YAML, "first\r\nsecond\n\r\n").text)
    }

    @Test
    fun preservesEmptyOutput() {
        assertEquals("", ConfigOutput(ConfigFormat.PROPERTIES, "").text)
    }

    @Test
    fun mapsYamlAndPropertiesToIdeFileTypes() {
        val manager = FileTypeManager.getInstance()
        assertEquals(manager.getFileTypeByExtension("yaml"), ConfigOutput(ConfigFormat.YAML, "value: 1").fileType())
        assertEquals(manager.getFileTypeByExtension("properties"), ConfigOutput(ConfigFormat.PROPERTIES, "value=1").fileType())
    }
}
