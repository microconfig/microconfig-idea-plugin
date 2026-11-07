package io.microconfig.plugin.actions.resolve.impl

import io.microconfig.plugin.EditorActionTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ResolveActionTest : EditorActionTestCase() {
    @Test
    fun resolvesPlaceholderUnderCaretForEveryEnvironment() {
        openWithCaretAfter("components/payments/application.yaml", "\${eureka@server.p")

        fixture.performEditorAction(ACTION)

        assertEquals(
            "\${eureka@server.port}\n\nDefault: 9761\n\nValue: 8761\nEnvs: [dev]\n\n",
            informationHint(),
        )
    }

    @Test
    fun resolvesEntireYamlValueWhenCaretIsOutsidePlaceholder() {
        openWithCaretAfter("components/payments/application.yaml", "  url:")

        fixture.performEditorAction(ACTION)

        assertEquals(
            "url\n\nDefault: http://localhost:9761\n\nValue: http://localhost:8761\nEnvs: [dev]\n\n",
            informationHint(),
        )
    }

    @Test
    fun resolvesEntirePropertiesValue() {
        writeRepoFile("components/payments/application.properties", "url=http://\${eureka@server.host}:\${eureka@server.port}\n")
        openWithCaretAfter("components/payments/application.properties", "url=")

        fixture.performEditorAction(ACTION)

        assertEquals(
            "url\n\nDefault: http://localhost:9761\n\nValue: http://localhost:8761\nEnvs: [dev]\n\n",
            informationHint(),
        )
    }

    @Test
    fun limitsHintsToEnvironmentsInTheFileName() {
        writeRepoFile("components/payments/application.prod.yaml", "url: \${eureka@server.port}\n")
        openWithCaretAfter("components/payments/application.prod.yaml", "\${eureka@server.p")

        fixture.performEditorAction(ACTION)

        assertEquals("\${eureka@server.port}\n\nValue: 9761\nEnvs: [prod]\n\n", informationHint())
    }

    @Test
    fun resolvesSelfReferencesAndExplicitEnvironments() {
        writeRepoFile("components/eureka/application.properties", "self=\${this[prod]@server.port}\n")
        openWithCaretAfter("components/eureka/application.properties", "\${this[prod]@server.p")

        fixture.performEditorAction(ACTION)

        assertEquals("\${this[prod]@server.port}\n\nDefault: 9761\n\n", informationHint())
    }

    @Test
    fun resolvesLiteralValueWithoutEnvironmentDuplicates() {
        openWithCaretAfter("components/eureka/application.yaml", "host:")

        fixture.performEditorAction(ACTION)

        assertEquals("host\n\nDefault: localhost\n\n", informationHint())
    }

    @Test
    fun reportsUnresolvablePlaceholderInTheHint() {
        writeRepoFile("components/payments/application.properties", "url=\${missing@host}\n")
        openWithCaretAfter("components/payments/application.properties", "\${missing@h")

        fixture.performEditorAction(ACTION)

        assertEquals("\${missing@host}\n\nDefault: Can't resolve\n\n", informationHint())
    }

    @Test
    fun resolvesExpressionInsidePlaceholderDefault() {
        writeRepoFile("components/payments/reference.properties", "value=\${eureka@missing:#{1 + 2}}\n")
        openWithCaretAfter("components/payments/reference.properties", "\${eureka@m")

        fixture.performEditorAction(ACTION)

        assertEquals("\${eureka@missing:#{1 + 2}}\n\nDefault: 3\n\n", informationHint())
    }

    @Test
    fun resolvesLineWhenCaretIsInAnExternalTemplate() {
        writeRepoFile("components/payments/reference.properties", "value=\${EXTERNAL} \${eureka@server.port}\n")
        openWithCaretAfter("components/payments/reference.properties", "\${EXT")

        fixture.performEditorAction(ACTION)

        assertTrue(informationHint().startsWith("value\n\nDefault: \${EXTERNAL} 9761"))
    }

    @Test
    fun reportsMalformedPropertyAsAnErrorHint() {
        writeRepoFile("components/payments/application.properties", "invalid property\n")
        openWithCaretAfter("components/payments/application.properties", "invalid")

        fixture.performEditorAction(ACTION)

        assertTrue(errorHint().contains("Incorrect delimiter in 'invalid property'"))
    }

    @Test
    fun groupsEqualValuesAndKeepsInputMapUnchanged() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:")
        val values = linkedMapOf("" to "default", "dev" to "default", "prod" to "other", "staging" to "other")
        val original = values.toMap()

        Hints.showHint("url", values, editorContext())

        assertEquals("url\n\nDefault: default\n\nValue: other\nEnvs: [prod, staging]\n\n", informationHint())
        assertEquals(original, values)
    }

    @Test
    fun reportsEmptyResolutionResult() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:")

        Hints.showHint("url", emptyMap(), editorContext())

        assertEquals("Can't resolve 'url'", errorHint())
    }

    @Test
    fun groupsEnvironmentValuesWithoutDefault() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:")

        Hints.showHint("url", linkedMapOf("dev" to "same", "prod" to "same"), editorContext())

        assertEquals("url\n\nValue: same\nEnvs: [dev, prod]\n\n", informationHint())
        assertFalse(informationHint().contains("Default:"))
    }

    companion object {
        private const val ACTION = "Microconfig.ResolveAction"
    }
}
