package io.microconfig.plugin.actions.jumps;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.microconfig.plugin.ActionHandler;
import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.actions.placeholders.PlaceholderBorders;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import static io.microconfig.plugin.utils.FileUtil.toPsiFile;
import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;

@RequiredArgsConstructor
public class JumpToPlaceholder implements ActionHandler {
    private final MicroconfigApi api;
    private final PluginContext context;

    @Override
    public void onAction() {
        String placeholder = PlaceholderBorders.borders(context.currentLine(), context.currentColumn()).value();
        if (placeholder == null || !api.navigatable(placeholder)) return; //todo maybe print a warning

        FilePosition filePosition = api.findPlaceholderSource(placeholder, context.currentFile(), context.projectDir());
        System.out.println("Resolved file position " + filePosition);
        VirtualFile virtualFile = toVirtualFile(filePosition.getFile());
        PsiFile psiFile = toPsiFile(context.getProject(), virtualFile);
        psiFile.navigate(true);

        moveToLine(context.getProject(), filePosition.getLineNumber());
    }

    private void moveToLine(Project project, int line) {
        FileEditorManager.getInstance(project)
                .getSelectedTextEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(line, 0));
    }
}
