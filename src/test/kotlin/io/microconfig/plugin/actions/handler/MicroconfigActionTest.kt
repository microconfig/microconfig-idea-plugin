package io.microconfig.plugin.actions.handler

import com.intellij.openapi.actionSystem.CommonDataKeys.PROJECT
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.testFramework.TestActionEvent
import io.microconfig.plugin.EditorActionTestCase
import io.microconfig.plugin.microconfig.PluginContext
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockingDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MicroconfigActionTest : EditorActionTestCase() {
    @ParameterizedTest
    @ValueSource(strings = ["Microconfig.JumpToAction", "Microconfig.ResolveAction", "Microconfig.PreviewAction"])
    fun registeredActionsIgnoreEventsWithoutEditor(id: String) {
        val action = ActionManager.getInstance().getAction(id)

        assertDoesNotThrow { action.actionPerformed(TestActionEvent.createTestEvent(action, DataContext.EMPTY_CONTEXT)) }
        assertTrue(mockingDetails(hints).invocations.isEmpty())
    }

    @Test
    fun ignoresIncompleteEditorContext() {
        val action = object : MicroconfigAction() {
            override fun chooseHandler(context: PluginContext): ActionHandler = error("Must not choose a handler")
        }
        val event = TestActionEvent.createTestEvent(action, DataContext { id ->
            if (PROJECT.`is`(id)) project() else null
        })

        assertDoesNotThrow { action.actionPerformed(event) }
        assertTrue(mockingDetails(hints).invocations.isEmpty())
    }

    @Test
    fun usesExceptionTypeWhenErrorHasNoMessage() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:")

        editorContext().showErrorHint(IllegalStateException())

        assertEquals("IllegalStateException", errorHint())
    }
}
