package io.microconfig.plugin.utils;

import com.intellij.openapi.editor.Document;
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

    public static String componentType(String currentFileName) {
        int lastDot = currentFileName.lastIndexOf('.');
        if (lastDot >= 0) return currentFileName.substring(lastDot);

        throw new PluginException("Current file doesn't have an extension. Unable to resolve component type.");
    }

}
