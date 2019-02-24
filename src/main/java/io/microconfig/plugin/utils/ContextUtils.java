package io.microconfig.plugin.utils;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.PluginException;

public class ContextUtils {
    public static String currentLine(PluginContext context) {
        Document doc = context.editor.getDocument();

        int lineNum = context.caret.getLogicalPosition().line;
        int start = doc.getLineStartOffset(lineNum);
        int end = doc.getLineEndOffset(lineNum);
        return doc.getCharsSequence().subSequence(start, end).toString();
    }

    public static String fileExtension(String currentFileName) {
        int lastDot = currentFileName.lastIndexOf('.');
        if (lastDot >= 0) return currentFileName.substring(lastDot);

        throw new PluginException("Current file doesn't have an extension. Unable to resolve component type.");
    }

    public static void moveToLineColumn(Project project, int line, int column) {
        FileEditorManager.getInstance(project)
                .getSelectedTextEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(line, column));
    }

    public static void showInfoHing(Editor editor, String message) {
        HintManager.getInstance().showInformationHint(editor, message);
    }

    public static void showErrorHing(Editor editor, String message) {
        HintManager.getInstance().showErrorHint(editor, message);
    }
}