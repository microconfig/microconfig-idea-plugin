package io.microconfig.plugin;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Optional;

import static com.intellij.openapi.actionSystem.CommonDataKeys.*;
import static com.intellij.openapi.ui.Messages.showInfoMessage;
import static java.util.Optional.*;

public class ResolveAction extends AnAction {
    private final FileFinder fileFinder = new FileFinder();
    private final ComponentNameResolver nameResolver = new ComponentNameResolver();

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(EDITOR);
        VirtualFile editorFile = event.getData(VIRTUAL_FILE);

        if (project == null || editor == null || editorFile == null) return;

        try {
            currentLine(event)
                    .flatMap(nameResolver::resolve)
                    .flatMap(cn -> fileFinder.resolveComponent(project, cn))
                    .flatMap(dir -> fileFinder.findComponentFile(project, dir, componentType(editorFile)))
                    .ifPresent(f -> f.navigate(true));
            HintManager.getInstance().showInformationHint(editor, "navigated");
        } catch (PluginException e) {
            showInfoMessage(e.getMessage(), "Microconfig Error");
//            HintManager.getInstance().showErrorHint(editor, e.getMessage());
        }
    }

    private Optional<String> currentLine(AnActionEvent event) {
        Editor editor = event.getData(EDITOR);
        Caret caret = event.getData(CARET);
        if (editor == null || caret == null) return empty();

        Document doc = editor.getDocument();
        int lineNum = caret.getLogicalPosition().line;

        int start = doc.getLineStartOffset(lineNum);
        int end = doc.getLineEndOffset(lineNum);
        return of(doc.getCharsSequence().subSequence(start, end).toString());
    }

    private String componentType(VirtualFile file) {
        int lastDot = file.getName().lastIndexOf('.');
        if (lastDot < 0) {
            throw new PluginException("Current file doesn't have an extension. Unable to resolve component type.");
        }

        return file.getName().substring(lastDot);
    }
}