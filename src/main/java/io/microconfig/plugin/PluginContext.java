package io.microconfig.plugin;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.intellij.openapi.actionSystem.CommonDataKeys.*;
import static io.microconfig.plugin.utils.FileUtil.toFile;

@RequiredArgsConstructor
public class PluginContext {
    @Getter
    private final Project project;
    private final Editor editor;
    private final Caret caret;
    private final VirtualFile editorFile;

    public PluginContext(AnActionEvent event) {
        this(event.getProject(), event.getData(EDITOR), event.getData(CARET), event.getData(VIRTUAL_FILE));
    }

    public boolean notFull() {
        return project == null || editor == null || editorFile == null || caret == null;
    }

    public File projectDir() {
        return toFile(project.getBaseDir());
    }

    public File currentFile() {
        return toFile(editorFile);
    }

    public String currentLine() {
        Document doc = editor.getDocument();

        int lineNum = caret.getLogicalPosition().line;
        int start = doc.getLineStartOffset(lineNum);
        int end = doc.getLineEndOffset(lineNum);
        return doc.getCharsSequence().subSequence(start, end).toString();
    }

    public int currentColumn() {
        return caret.getLogicalPosition().column;
    }

    public void showInfoHing(String message) {
        HintManager.getInstance().showInformationHint(editor, message);
    }

    public void showErrorHing(Exception e) {
        showErrorHing(e.getMessage());
    }

    public void showErrorHing(String message) {
        HintManager.getInstance().showErrorHint(editor, message);
    }
}