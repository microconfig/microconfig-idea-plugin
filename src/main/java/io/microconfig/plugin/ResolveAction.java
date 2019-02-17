package io.microconfig.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Optional;

import static com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE;
import static com.intellij.openapi.ui.Messages.showInfoMessage;
import static java.util.Optional.*;

public class ResolveAction extends AnAction {
    private final FileFinder fileFinder = new FileFinder();
    private final ComponentNameResolver nameResolver = new ComponentNameResolver();

    public ResolveAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        try {
            currentLine(event)
                    .flatMap(nameResolver::resolve)
                    .flatMap(cn -> fileFinder.resolveComponent(project, cn))
                    .flatMap(dir -> fileFinder.findComponentFile(project, dir, componentType(event)))
                    .ifPresent(f -> f.navigate(true));
        } catch (PluginException e) {
            showInfoMessage(project, e.getMessage(), "Microconfig Error");
        }
    }

    private Optional<String> currentLine(AnActionEvent event) {
        Optional<Document> document = ofNullable(event.getData(LangDataKeys.EDITOR)).map(Editor::getDocument);
        Optional<LogicalPosition> position = ofNullable(event.getData(LangDataKeys.CARET)).map(Caret::getLogicalPosition);
        if (!document.isPresent() || !position.isPresent()) return empty();

        int lineNum = position.get().line;
        Document doc = document.get();

        int start = doc.getLineStartOffset(lineNum);
        int end = doc.getLineEndOffset(lineNum);
        String line = doc.getCharsSequence().subSequence(start, end).toString();
        return of(line);
    }

    private String componentType(AnActionEvent event) {
        VirtualFile file = event.getData(VIRTUAL_FILE);
        int lastDot = file.getName().lastIndexOf('.');
        if (lastDot < 0) {
            throw new PluginException("Current file doesn't have an extension. Unable to resolve component type.");
        }
        return file.getName().substring(lastDot);
    }
}