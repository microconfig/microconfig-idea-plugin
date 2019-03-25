package io.microconfig.plugin.actions.common;

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
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;

import static com.intellij.openapi.actionSystem.CommonDataKeys.*;
import static io.microconfig.plugin.utils.FileUtil.*;
import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isDigit;

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
        return toFile(project.getBaseDir());
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
        int column = currentColumn();

        IntPredicate isAllowedNameSymbol = c -> isAlphabetic(c)
                || isDigit(c)
                || c == '_'
                || c == '-';

        IntSupplier startIndex = () -> {
            for (int i = column - 1; i >= 0; i--) {
                if (!isAllowedNameSymbol.test(line.charAt(i))) return i;
            }
            return -1;
        };
        IntSupplier endIndex = () -> {
            for (int i = column - 1; i < line.length(); i++) {
                if (!isAllowedNameSymbol.test(line.charAt(i))) return i;
            }
            return line.length();
        };

        int start = startIndex.getAsInt();
        int end = endIndex.getAsInt();
        return start < end ? line.substring(start + 1, end) : "";
    }

    public void navigateTo(File file) {
        toPsiFile(project, toVirtualFile(file))
                .navigate(true);
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