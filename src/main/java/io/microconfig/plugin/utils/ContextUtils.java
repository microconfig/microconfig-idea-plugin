package io.microconfig.plugin.utils;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import io.microconfig.plugin.PluginException;

public class ContextUtils {
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
}