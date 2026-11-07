package io.microconfig.plugin.microconfig

import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.testFramework.TestActionEvent
import io.microconfig.plugin.EditorActionTestCase
import io.microconfig.core.properties.FileBasedComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class PluginContextTest : EditorActionTestCase() {
    @Test
    fun readsEditorProjectFileLineAndColumnFromActionEvent() {
        openWithCaretAfter(ENV, "- pay")
        val editor = fixture.editor
        val file = FileDocumentManager.getInstance().getFile(editor.document)
        val data = SimpleDataContext.builder()
            .add(CommonDataKeys.PROJECT, project())
            .add(CommonDataKeys.EDITOR, editor)
            .add(CommonDataKeys.CARET, editor.caretModel.primaryCaret)
            .add(CommonDataKeys.VIRTUAL_FILE, file)
            .build()

        val context = PluginContext(TestActionEvent.createTestEvent(data))

        assertFalse(context.notFull())
        assertSame(project(), context.project)
        assertSame(editor, context.editor)
        assertEquals(projectDir, context.projectDir())
        assertEquals(repoFile(ENV), context.currentFile())
        assertEquals("    - payments", context.currentLine())
        assertEquals(9, context.currentColumn())
    }

    @Test
    fun detectsEveryMissingContextPart() {
        openWithCaretAfter(ENV, "")
        val editor = fixture.editor
        val caret = editor.caretModel.primaryCaret
        val file = FileDocumentManager.getInstance().getFile(editor.document)

        assertTrue(PluginContext(null, editor, caret, file).notFull())
        assertTrue(PluginContext(project(), null, caret, file).notFull())
        assertTrue(PluginContext(project(), editor, null, file).notFull())
        assertTrue(PluginContext(project(), editor, caret, null).notFull())
    }

    @Test
    fun tokenUnderCaret() {
        openWithCaretAfter(ENV, "- pay")
        assertEquals("payments", editorContext().currentToken())
    }

    @Test
    fun tokenRightAfterCaret() {
        openWithCaretAfter(ENV, "- ")
        assertEquals("eureka", editorContext().currentToken())
    }

    @Test
    fun tokenAtLineStart() {
        openWithCaretAt(ENV, 0, 0)
        assertEquals("main", editorContext().currentToken())
    }

    @Test
    fun noTokenAtIndentStart() {
        openWithCaretAt(ENV, 3, 0)
        assertEquals("", editorContext().currentToken())
    }

    @Test
    fun tokenWithCaretBeyondLineEnd() {
        openWithCaretAt(ENV, 3, 40)
        assertEquals("payments", editorContext().currentToken())
    }

    @Test
    fun tokenIncludesUnicodeLettersHyphensUnderscoresAndDigits() {
        writeRepoFile("envs/token.yaml", "name: клиент-api_2\n")
        openWithCaretAfter("envs/token.yaml", "кли")

        assertEquals("клиент-api_2", editorContext().currentToken())
    }

    @Test
    fun tokenAtEmptyLineIsEmpty() {
        writeRepoFile("envs/empty.yaml", "\n")
        openWithCaretAfter("envs/empty.yaml", "")

        assertEquals("", editorContext().currentToken())
    }

    @Test
    fun navigatesToFileAndDeclaredLine() {
        openWithCaretAfter(ENV, "")
        val context = editorContext()
        val file = repoFile("components/eureka/application.yaml")

        context.navigateTo(file)
        assertEquals(file, selectedFile())

        FilePosition.positionFromFileSource(FileBasedComponent(file, 2, true, "app", "dev")).moveToPosition(context)
        assertEquals(file, selectedFile())
        assertEquals(2, FileEditorManager.getInstance(project()).selectedTextEditor!!.caretModel.logicalPosition.line)
    }

    @Test
    fun displaysInformationAndErrorHints() {
        openWithCaretAfter(ENV, "")

        editorContext().showInfoHint("Resolved value")
        editorContext().showErrorHint(IllegalArgumentException("Invalid configuration"))

        assertEquals("Resolved value", informationHint())
        assertEquals("Invalid configuration", errorHint())
    }

    @Test
    fun navigationToMissingFileFails() {
        openWithCaretAt(ENV, 0, 0)
        val missing = repoFile("components/missing/application.yaml")
        val error = assertThrows(IllegalStateException::class.java) { editorContext().navigateTo(missing) }
        assertEquals("Can't find file '$missing'", error.message)
    }

    private fun openWithCaretAt(path: String, line: Int, column: Int) {
        openWithCaretAfter(path, "")
        fixture.editor.settings.isVirtualSpace = true
        fixture.editor.caretModel.moveToLogicalPosition(LogicalPosition(line, column))
    }

    companion object {
        private const val ENV = "envs/dev.yaml"
    }
}
