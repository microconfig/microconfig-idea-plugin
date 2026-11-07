package io.microconfig.plugin

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.junit5.RunInEdt
import io.microconfig.plugin.microconfig.PluginContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import java.io.File

/** Runs the plugin inside a headless IDE with a copy of the test repository in a temporary project. */
@RunInEdt(writeIntent = true)
abstract class MicroconfigRepoTestCase {
    protected lateinit var fixture: CodeInsightTestFixture
    protected lateinit var projectDir: File
    protected lateinit var repoDir: File

    @BeforeEach
    fun setUpFixture() {
        fixture = createFixture()
        fixture.setUp()
        projectDir = File(checkNotNull(project().basePath))
        repoDir = File(projectDir, "repo")
        copyRepo()
    }

    @AfterEach
    fun tearDownFixture() {
        try {
            FileUtil.delete(repoDir)
        } finally {
            fixture.tearDown()
        }
    }

    protected fun project(): Project = fixture.project

    protected fun repoFile(path: String): File = File(repoDir, path)

    protected fun writeRepoFile(path: String, text: String): File {
        val file = repoFile(path)
        file.parentFile.mkdirs()
        file.writeText(text)
        LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)?.refresh(false, false)
        return file
    }

    protected fun replaceEditorText(text: String) {
        WriteCommandAction.runWriteCommandAction(project()) {
            fixture.editor.document.setText(text)
        }
    }

    protected fun openWithCaretAfter(path: String, textBeforeCaret: String) {
        val file = checkNotNull(LocalFileSystem.getInstance().findFileByIoFile(repoFile(path)))
        fixture.openFileInEditor(file)
        val index = fixture.editor.document.text.indexOf(textBeforeCaret)
        assertTrue(index >= 0, "'$textBeforeCaret' not found in $path")
        fixture.editor.caretModel.moveToOffset(index + textBeforeCaret.length)
    }

    protected fun editorContext(): PluginContext {
        val editor = fixture.editor
        val file = FileDocumentManager.getInstance().getFile(editor.document)
        return PluginContext(project(), editor, editor.caretModel.primaryCaret, file)
    }

    protected fun selectedFile(): File {
        val selected = FileEditorManager.getInstance(project()).selectedFiles
        assertEquals(1, selected.size)
        return File(selected.single().path)
    }

    private fun createFixture(): CodeInsightTestFixture {
        val factory = IdeaTestFixtureFactory.getFixtureFactory()
        val projectFixture = factory.createFixtureBuilder(javaClass.simpleName).fixture
        return factory.createCodeInsightFixture(projectFixture)
    }

    private fun copyRepo() {
        FileUtil.delete(repoDir)
        FileUtil.copyDir(File(checkNotNull(javaClass.getResource("/repo")).toURI()), repoDir)
        checkNotNull(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(repoDir)).refresh(false, true)
    }
}
