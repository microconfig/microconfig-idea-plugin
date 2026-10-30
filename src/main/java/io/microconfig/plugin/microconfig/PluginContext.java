package io.microconfig.plugin.microconfig;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.intellij.openapi.actionSystem.CommonDataKeys.*;
import static io.microconfig.plugin.utils.FileUtil.*;
import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isDigit;
import static java.lang.Math.min;

@RequiredArgsConstructor
public class PluginContext {
    @Getter
    private final Project project;
    @Getter
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
        return toFile(project.getBasePath());
    }

    public File currentFile() {
        return toFile(editorFile);
    }

    public String currentLine() {
        Document doc = editor.getDocument();
        int lineNum = caret.getLogicalPosition().line;
        return doc.getCharsSequence()
                .subSequence(
                        doc.getLineStartOffset(lineNum),
                        doc.getLineEndOffset(lineNum)
                ).toString();
    }

    public int currentColumn() {
        return caret.getLogicalPosition().column;
    }

    public String currentToken() {
        String line = currentLine();
        int column = min(currentColumn(), line.length());
        return line.substring(tokenStart(line, column), tokenEnd(line, column));
    }

    private int tokenStart(String line, int column) {
        int start = column;
        while (start > 0 && isNameSymbol(line.charAt(start - 1))) start--;
        return start;
    }

    private int tokenEnd(String line, int column) {
        int end = column;
        while (end < line.length() && isNameSymbol(line.charAt(end))) end++;
        return end;
    }

    private boolean isNameSymbol(char c) {
        return isAlphabetic(c) || isDigit(c) || c == '_' || c == '-';
    }

    public void navigateTo(File file) {
        new OpenFileDescriptor(project, existingVirtualFile(file)).navigate(true);
    }

    public void navigateTo(File file, int line) {
        new OpenFileDescriptor(project, existingVirtualFile(file), line, 0).navigate(true);
    }

    private VirtualFile existingVirtualFile(File file) {
        VirtualFile virtualFile = toVirtualFile(file);
        if (virtualFile == null) throw new IllegalStateException("Can't find file '" + file + "'");
        return virtualFile;
    }

    public void showInfoHint(String message) {
        HintManager.getInstance().showInformationHint(editor, message);
    }

    public void showErrorHint(Exception e) {
        showErrorHint(e.getMessage());
    }

    public void showErrorHint(String message) {
        HintManager.getInstance().showErrorHint(editor, message);
    }
}