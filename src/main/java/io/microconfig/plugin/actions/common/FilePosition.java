package io.microconfig.plugin.actions.common;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.plugin.utils.FileUtil.toPsiFile;
import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;

@RequiredArgsConstructor
public class FilePosition {
    private final File file;
    private final int lineNumber;

    public void moveToPosition(Project project) {
        VirtualFile virtualFile = toVirtualFile(file);
        PsiFile psiFile = toPsiFile(project, virtualFile);
        psiFile.navigate(true);

        moveToLine(project, lineNumber);
    }

    private void moveToLine(Project project, int line) {
        FileEditorManager.getInstance(project)
                .getSelectedTextEditor()
                .getCaretModel()
                .moveToLogicalPosition(new LogicalPosition(line, 0));
    }
}