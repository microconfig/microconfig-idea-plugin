package io.microconfig.plugin.actions.jump.impl

import com.intellij.openapi.fileEditor.FileEditorManager
import io.microconfig.plugin.EditorActionTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockingDetails

class JumpActionTest : EditorActionTestCase() {
    @Test
    fun jumpFromEnvToComponent() {
        openWithCaretAfter("envs/dev.yaml", "- pay")
        fixture.performEditorAction(JUMP_ACTION)
        assertEquals(repoFile("components/payments/application.yaml"), selectedFile())
    }

    @Test
    fun jumpToInclude() {
        openWithCaretAfter("components/payments/application.yaml", "#include eur")
        fixture.performEditorAction(JUMP_ACTION)
        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile())
    }

    @Test
    fun jumpToPlaceholderSource() {
        openWithCaretAfter("components/payments/application.yaml", "\${eureka@server.h")
        fixture.performEditorAction(JUMP_ACTION)
        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile())
        assertEquals(1, FileEditorManager.getInstance(project()).selectedTextEditor!!.caretModel.logicalPosition.line)
    }

    @Test
    fun selectsIncludedComponentUnderCaret() {
        writeRepoFile("components/payments/application.properties", "#include payments, eureka\n")
        openWithCaretAfter("components/payments/application.properties", "#include payments, eur")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile())
    }

    @Test
    fun followsExplicitIncludeEnvironment() {
        writeRepoFile("components/payments/application.properties", "#include eureka[prod]\n")
        openWithCaretAfter("components/payments/application.properties", "#include eur")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.prod.yaml"), selectedFile())
    }

    @Test
    fun inheritsIncludeEnvironmentFromFileName() {
        writeRepoFile("components/payments/application.prod.yaml", "#include eureka\n")
        openWithCaretAfter("components/payments/application.prod.yaml", "#include eur")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.prod.yaml"), selectedFile())
    }

    @Test
    fun followsSelfReferencedPlaceholder() {
        writeRepoFile("components/eureka/reference.dev.properties", "port=\${this@server.port}\n")
        openWithCaretAfter("components/eureka/reference.dev.properties", "\${this@server.p")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile())
        assertEquals(2, FileEditorManager.getInstance(project()).selectedTextEditor!!.caretModel.logicalPosition.line)
    }

    @Test
    fun followsPlaceholderWithExplicitEnvironment() {
        writeRepoFile("components/payments/reference.properties", "port=\${eureka[prod]@server.port}\n")
        openWithCaretAfter("components/payments/reference.properties", "\${eureka[prod]@server.p")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.prod.yaml"), selectedFile())
        assertEquals(1, FileEditorManager.getInstance(project()).selectedTextEditor!!.caretModel.logicalPosition.line)
    }

    @Test
    fun fallsBackToComponentFileForMissingProperty() {
        writeRepoFile("components/payments/reference.properties", "port=\${eureka@missing}\n")
        openWithCaretAfter("components/payments/reference.properties", "\${eureka@m")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile())
        assertEquals(0, FileEditorManager.getInstance(project()).selectedTextEditor!!.caretModel.logicalPosition.line)
    }

    @Test
    fun findsOuterPlaceholderAfterNestedDefault() {
        writeRepoFile("components/payments/reference.dev.properties", "value=\${eureka@missing:\${payments@missing}suffix}\n")
        openWithCaretAfter("components/payments/reference.dev.properties", "suff")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile())
    }

    @Test
    fun jumpsFromJsonEnvironmentDescriptor() {
        writeRepoFile("envs/dev.json", "{\"main\": {\"components\": [\"payments\"]}}")
        openWithCaretAfter("envs/dev.json", "\"pay")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/payments/application.yaml"), selectedFile())
    }

    @Test
    fun ignoresEmptyTokenInEnvironmentDescriptor() {
        openWithCaretAfter("envs/dev.yaml", "")
        fixture.editor.caretModel.moveToLogicalPosition(com.intellij.openapi.editor.LogicalPosition(1, 0))

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("envs/dev.yaml"), selectedFile())
        assertTrue(mockingDetails(hints).invocations.isEmpty())
    }

    @Test
    fun ignoresOrdinaryConfigLine() {
        openWithCaretAfter("components/eureka/application.yaml", "host:")

        fixture.performEditorAction(JUMP_ACTION)

        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile())
        assertTrue(mockingDetails(hints).invocations.isEmpty())
    }

    @Test
    fun displaysMissingComponentError() {
        writeRepoFile("components/payments/reference.properties", "#include missing\n")
        openWithCaretAfter("components/payments/reference.properties", "#include mis")

        fixture.performEditorAction(JUMP_ACTION)

        assertTrue(errorHint().contains("missing"))
        assertEquals(repoFile("components/payments/reference.properties"), selectedFile())
    }

    companion object {
        private const val JUMP_ACTION = "Microconfig.JumpToAction"
    }
}
