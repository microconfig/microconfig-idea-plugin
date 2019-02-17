package io.microconfig.plugin;

import com.intellij.openapi.editor.Document;

public class ContextUtils {

    public static String currentLine(PluginContext context) {
        Document doc = context.editor.getDocument();

        int lineNum = context.caret.getLogicalPosition().line;
        int start = doc.getLineStartOffset(lineNum);
        int end = doc.getLineEndOffset(lineNum);
        return doc.getCharsSequence().subSequence(start, end).toString();
    }

    public static String componentType(PluginContext context) {
        int lastDot = context.editorFile.getName().lastIndexOf('.');
        if (lastDot >= 0) return context.editorFile.getName().substring(lastDot);

        throw new PluginException("Current file doesn't have an extension. Unable to resolve component type.");
    }

}
