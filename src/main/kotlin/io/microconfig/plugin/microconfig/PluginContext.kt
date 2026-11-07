package io.microconfig.plugin.microconfig

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.CARET
import com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR
import com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.microconfig.plugin.utils.toVirtualFile
import java.io.File

class PluginContext(
    private val actionProject: Project?,
    private val actionEditor: Editor?,
    private val caret: Caret?,
    private val editorFile: VirtualFile?,
) {
    constructor(event: AnActionEvent) : this(
        event.project, event.getData(EDITOR), event.getData(CARET), event.getData(VIRTUAL_FILE),
    )

    val project: Project get() = checkNotNull(actionProject)
    val editor: Editor get() = checkNotNull(actionEditor)

    fun notFull(): Boolean = actionProject == null || actionEditor == null || editorFile == null || caret == null

    fun projectDir(): File = File(checkNotNull(project.basePath))

    fun currentFile(): File = File(checkNotNull(editorFile).path)

    fun currentLine(): String {
        val document = editor.document
        val line = checkNotNull(caret).logicalPosition.line
        return document.charsSequence.subSequence(
            document.getLineStartOffset(line), document.getLineEndOffset(line),
        ).toString()
    }

    fun currentColumn(): Int = checkNotNull(caret).logicalPosition.column

    fun currentToken(): String {
        val line = currentLine()
        val column = currentColumn().coerceAtMost(line.length)
        return line.substring(tokenStart(line, column), tokenEnd(line, column))
    }

    fun navigateTo(file: File) {
        OpenFileDescriptor(project, existingVirtualFile(file)).navigate(true)
    }

    fun navigateTo(file: File, line: Int) {
        OpenFileDescriptor(project, existingVirtualFile(file), line, 0).navigate(true)
    }

    fun showInfoHint(message: String) {
        HintManager.getInstance().showInformationHint(editor, message)
    }

    fun showErrorHint(error: Exception) {
        showErrorHint(error.message ?: error.javaClass.simpleName)
    }

    fun showErrorHint(message: String) {
        HintManager.getInstance().showErrorHint(editor, message)
    }

    private fun tokenStart(line: String, column: Int): Int {
        var start = column
        while (start > 0 && isNameSymbol(line[start - 1])) start--
        return start
    }

    private fun tokenEnd(line: String, column: Int): Int {
        var end = column
        while (end < line.length && isNameSymbol(line[end])) end++
        return end
    }

    private fun isNameSymbol(character: Char): Boolean =
        Character.isAlphabetic(character.code) || character.isDigit() || character == '_' || character == '-'

    private fun existingVirtualFile(file: File): VirtualFile =
        toVirtualFile(file) ?: error("Can't find file '$file'")
}
