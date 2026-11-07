package io.microconfig.plugin.microconfig.impl

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileTypeManager
import io.microconfig.plugin.MicroconfigRepoTestCase
import io.microconfig.plugin.microconfig.MicroconfigApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml
import java.util.Properties

class ConfigFormatsTest : MicroconfigRepoTestCase() {
    private val api = MicroconfigApi.create()

    @Test
    fun buildsPropertiesWithIncludesOverridesAndEscapedValues() {
        val file = createPropertiesComponents()

        val output = api.buildConfigs(file, projectDir, "prod")

        val properties = Properties().apply { load(output.text.reader()) }
        assertEquals("jdbc://db.local:6432", properties.getProperty("database.url"))
        assertEquals("Привет", properties.getProperty("greeting"))
        assertEquals("first\nsecond", properties.getProperty("message"))
        assertEquals("db.local", properties.getProperty("host"))
        assertEquals(FileTypeManager.getInstance().getFileTypeByExtension("properties"), output.fileType())
    }

    @Test
    fun resolvesPropertiesForEachEnvironment() {
        val file = createPropertiesComponents()

        val values = api.resolveFullLineForEachEnv("jdbc://\${database@host}:\${database@port}", file, projectDir)

        assertEquals("jdbc://db.local:5432", values["dev"])
        assertEquals("jdbc://db.local:6432", values["prod"])
    }

    @Test
    fun buildsUnsavedPropertiesContainingUnicode() {
        val file = writeRepoFile("components/unicode/application.properties", "greeting=old\n")
        openWithCaretAfter("components/unicode/application.properties", "")
        replaceEditorText("greeting=Привет\n")

        val output = api.buildConfigs(file, projectDir, "dev")

        assertEquals("Привет", Properties().apply { load(output.text.reader()) }.getProperty("greeting"))
    }

    @Test
    fun buildsYamlFromCustomExtension() {
        configureManifestType()
        val file = writeRepoFile("components/image/values.manifest", "image: original\nreplicas: 1\n")
        writeRepoFile("components/image/values.prod.manifest", "replicas: 3\n")

        val output = api.buildConfigs(file, projectDir, "prod")

        assertEquals(mapOf("image" to "original", "replicas" to 3), Yaml().load<Map<String, Any>>(output.text))
        assertEquals(FileTypeManager.getInstance().getFileTypeByExtension("yaml"), output.fileType())
    }

    @Test
    fun buildsPropertiesFromCustomExtension() {
        configureManifestType()
        val file = writeRepoFile("components/image/values.manifest", "image=original\nreplicas=1\n")
        writeRepoFile("components/image/values.prod.manifest", "replicas=3\n")

        val output = api.buildConfigs(file, projectDir, "prod")

        val properties = Properties().apply { load(output.text.reader()) }
        assertEquals("original", properties.getProperty("image"))
        assertEquals("3", properties.getProperty("replicas"))
        assertEquals(FileTypeManager.getInstance().getFileTypeByExtension("properties"), output.fileType())
    }

    @Test
    fun navigatesToExplicitCustomConfigType() {
        configureManifestType()
        writeRepoFile("components/image/values.manifest", "image: original\nreplicas: 1\n")
        writeRepoFile("components/payments/reference.properties", "image=\${manifest::image@image}\n")
        openWithCaretAfter("components/payments/reference.properties", "image=")

        api.findPlaceholderSource("\${manifest::image@image}", editorContext().currentFile(), projectDir)
            .moveToPosition(editorContext())

        assertEquals(repoFile("components/image/values.manifest"), selectedFile())
        assertEquals(0, FileEditorManager.getInstance(project()).selectedTextEditor!!.caretModel.logicalPosition.line)
    }

    @Test
    fun findsComponentWithOnlyCustomConfigFiles() {
        configureManifestType()
        writeRepoFile("components/image/values.manifest", "image: original\n")
        val override = writeRepoFile("components/image/values.prod.manifest", "image: production\n")

        assertEquals(override, api.findAnyComponentFile("image", "prod", projectDir))
    }

    @Test
    fun includesCustomConfigType() {
        configureManifestType()
        val source = writeRepoFile("components/image/values.manifest", "image: original\n")
        val file = writeRepoFile("components/payments/values.manifest", "#include image\nreplicas: 3\n")

        assertEquals(source, api.findIncludeSource("#include image", 12, file, projectDir))
        assertEquals(mapOf("image" to "original", "replicas" to 3), Yaml().load<Map<String, Any>>(
            api.buildConfigs(file, projectDir, "prod").text,
        ))
    }

    @Test
    fun readsUnsavedCustomConfigDescriptor() {
        writeRepoFile("microconfig.yaml", "configTypes: [old]\n")
        openWithCaretAfter("microconfig.yaml", "")
        replaceEditorText(MANIFEST_TYPE)
        val file = writeRepoFile("components/image/values.manifest", "image: original\n")

        assertEquals(mapOf("image" to "original"), Yaml().load<Map<String, Any>>(
            api.buildConfigs(file, projectDir, "dev").text,
        ))
    }

    @Test
    fun supportsShorthandCustomConfigType() {
        writeRepoFile("microconfig.yaml", "configTypes: [custom]\n")
        val file = writeRepoFile("components/image/values.custom", "image=original\n")

        val output = api.buildConfigs(file, projectDir, "dev")

        assertEquals("original", Properties().apply { load(output.text.reader()) }.getProperty("image"))
    }

    @Test
    fun resolvesExplicitCustomConfigTypeForEachEnvironment() {
        configureManifestType()
        writeRepoFile("components/image/values.manifest", "replicas: 1\n")
        writeRepoFile("components/image/values.prod.manifest", "replicas: 3\n")
        val file = writeRepoFile("components/payments/values.manifest", "replicas: \${manifest::image@replicas}\n")

        val values = api.resolvePlaceholderForEachEnv("\${manifest::image@replicas}", file, projectDir)

        assertEquals("1", values["dev"])
        assertEquals("3", values["prod"])
        assertEquals(mapOf("replicas" to 3), Yaml().load<Map<String, Any>>(api.buildConfigs(file, projectDir, "prod").text))
    }

    @Test
    fun reportsMalformedCustomConfigDescriptor() {
        writeRepoFile("microconfig.yaml", "configTypes: [\n")
        val file = writeRepoFile("components/image/values.manifest", "image: original\n")

        val error = assertThrows(IllegalStateException::class.java) { api.buildConfigs(file, projectDir, "dev") }

        assertTrue(error.message!!.contains("Can't parse descriptor:"))
    }

    @Test
    fun reportsUnsupportedConfigExtension() {
        val file = writeRepoFile("components/payments/application.unknown", "key=value\n")

        val error = assertThrows(IllegalArgumentException::class.java) { api.buildConfigs(file, projectDir, "dev") }

        assertTrue(error.message!!.contains("Unsupported config extension '.unknown'"))
    }

    private fun createPropertiesComponents(): java.io.File {
        writeRepoFile("components/database/application.properties", "host=db.local\nport=5432\n")
        writeRepoFile("components/database/application.prod.properties", "port=6432\n")
        return writeRepoFile("components/catalog/application.properties", """
            #include database
            database.url=jdbc://${'$'}{database@host}:${'$'}{database@port}
            greeting=\u041f\u0440\u0438\u0432\u0435\u0442
            message=first\nsecond
        """.trimIndent() + "\n")
    }

    private fun configureManifestType() {
        writeRepoFile("microconfig.yaml", MANIFEST_TYPE)
    }

    companion object {
        private val MANIFEST_TYPE = """
            configTypes:
              - manifest:
                  sourceExtensions: [.manifest]
                  resultFileName: deployment
        """.trimIndent() + "\n"
    }
}
