package io.microconfig.plugin.microconfig.impl

import com.intellij.openapi.command.WriteCommandAction
import io.microconfig.plugin.MicroconfigRepoTestCase
import io.microconfig.plugin.microconfig.MicroconfigApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.io.File
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MicroconfigApiImplTest : MicroconfigRepoTestCase() {
    private val api: MicroconfigApi = MicroconfigApiImpl()

    @Test
    fun resolvePlaceholderForEachEnv() {
        val values = api.resolvePlaceholderForEachEnv(
            "\${eureka@server.port}", repoFile("components/payments/application.yaml"), projectDir,
        )
        assertEquals("8761", values["dev"])
        assertEquals("9761", values["prod"])
    }

    @Test
    fun buildConfigsForPreview() {
        val text = api.buildConfigs(repoFile("components/payments/application.yaml"), projectDir, "prod").text
        assertTrue(text.contains("http://localhost:9761"), text)
    }

    @Test
    fun componentWithoutConfigFiles() {
        val error = assertThrows(IllegalStateException::class.java) {
            api.findAnyComponentFile("empty", "dev", projectDir)
        }
        assertEquals("Can't find config files of component 'empty'", error.message)
    }

    @Test
    fun previewUsesUnsavedConfiguration() {
        openWithCaretAfter("components/eureka/application.prod.yaml", "")
        WriteCommandAction.runWriteCommandAction(project()) {
            fixture.editor.document.setText("server:\n  port: 7761\n")
        }
        val text = api.buildConfigs(repoFile("components/payments/application.yaml"), projectDir, "prod").text
        assertTrue(text.contains("http://localhost:7761"), text)
    }

    @Test
    fun includeUsesEnvironmentProfiles() {
        Files.writeString(repoFile("envs/prod.yaml").toPath(), "profiles: [dev]\nmain:\n  components:\n    - eureka\n    - payments\n")
        Files.writeString(repoFile("components/eureka/application.dev.yaml").toPath(), "server:\n  hostname: dev-host\n")
        Files.delete(repoFile("components/eureka/application.prod.yaml").toPath())
        val source = api.findIncludeSource("#include eureka", 12, repoFile("components/payments/application.prod.yaml"), projectDir)
        assertEquals(repoFile("components/eureka/application.dev.yaml"), source)
    }

    @Test
    fun listsConcreteEnvironmentsAndOmitsAbstractOnes() {
        writeRepoFile("envs/shared.yaml", "abstract: true\nmain:\n  components: [eureka]\n")

        assertEquals(setOf("dev", "prod"), api.getEnvs(projectDir))
    }

    @ParameterizedTest
    @CsvSource("application.yaml, NULL", "application.prod.yaml, prod", "application.dev.prod.properties, dev", "application, NULL", nullValues = ["NULL"])
    fun detectsEnvironmentFromFileName(name: String, env: String?) {
        assertEquals(env, api.detectEnvOr(File(name)))
    }

    @Test
    fun resolvesOnlyEnvironmentsSpecifiedByTheFileName() {
        val file = repoFile("components/payments/application.dev.prod.yaml")

        assertEquals(mapOf("dev" to "8761", "prod" to "9761"), api.resolveFullLineForEachEnv("\${eureka@server.port}", file, projectDir))
    }

    @Test
    fun literalLineNeedsNoEnvironments() {
        assertEquals(mapOf("" to "literal value"), api.resolveFullLineForEachEnv("literal value", repoFile("components/payments/application.yaml"), projectDir))
    }

    @Test
    fun returnsResolutionFailureForEveryEnvironment() {
        val values = api.resolvePlaceholderForEachEnv("\${missing@key}", repoFile("components/payments/application.yaml"), projectDir)

        assertEquals(setOf("", "dev", "prod"), values.keys)
        assertEquals(setOf("Can't resolve"), values.values.toSet())
    }

    @Test
    fun buildsPreviewWithVisibleExternalPlaceholderMarker() {
        val file = writeRepoFile("components/payments/external.properties", "token=\${EXTERNAL@secret}\n")

        val output = api.buildConfigs(file, projectDir, "dev")

        assertTrue(output.text.contains("\${EXTERNAL[UNRESOLVED]secret}"), output.text)
    }

    @Test
    fun reportsInvalidPlaceholderSyntax() {
        val error = assertThrows(IllegalStateException::class.java) {
            api.findPlaceholderSource("invalid", repoFile("components/payments/application.yaml"), projectDir)
        }

        assertEquals("Can't parse invalid", error.message)
    }

    @Test
    fun reportsMalformedInclude() {
        assertThrows(IllegalArgumentException::class.java) {
            api.findIncludeSource("#include ", 9, repoFile("components/payments/application.yaml"), projectDir)
        }
    }
}
