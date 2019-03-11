package io.microconfig.plugin.actions.common;

import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import io.microconfig.configs.Property;
import io.microconfig.configs.PropertySource;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.plugin.utils.FileUtil.toPsiFile;
import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;

@RequiredArgsConstructor
public class FilePosition {
    private final File file;
    private final int lineNumber;

    public static FilePosition positionFromProperty(Property property) {
        PropertySource source = property.getSource();
        return new FilePosition(
                new File(source.getSourceOfProperty()),
                source.getLine()
        );
    }

    public void moveToPosition(Project project) {
        openFile(project);
        moveToLine(project);
    }

    private void openFile(Project project) {
        toPsiFile(project, toVirtualFile(file))
                .navigate(true);
    }

    private void moveToLine(Project project) {
        FileEditorManager.getInstance(project)
                .getSelectedTextEditor()
                .getCaretModel()
                .moveToVisualPosition(new VisualPosition(lineNumber, 0));
    }
}