package io.microconfig.plugin;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;

public class ResolveAction extends AnAction {

    private final FileFinder fileFinder = new FileFinder();
    private final ComponentNameResolver nameResolver = new ComponentNameResolver();

    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            nameResolver.resolve(currentLine(context))
                .flatMap(cn -> fileFinder.resolveComponent(context.project, cn))
                .flatMap(dir -> fileFinder.findComponentFile(context.project, dir, componentType(context.editorFile)))
                .ifPresent(f -> f.navigate(true));
        } catch (PluginException e) {
            HintManager.getInstance().showErrorHint(context.editor, e.getMessage());
        } catch (NullPointerException e) {
            //ignore
        }
    }

    private String currentLine(PluginContext context) {
        Document doc = context.editor.getDocument();
        int lineNum = context.caret.getLogicalPosition().line;

        int start = doc.getLineStartOffset(lineNum);
        int end = doc.getLineEndOffset(lineNum);
        return doc.getCharsSequence().subSequence(start, end).toString();
    }

    private String componentType(VirtualFile file) {
        int lastDot = file.getName().lastIndexOf('.');
        if (lastDot >= 0) return file.getName().substring(lastDot);

        throw new PluginException("Current file doesn't have an extension. Unable to resolve component type.");
    }

}