package io.microconfig.plugin.microconfig;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import io.microconfig.core.properties.FileBasedComponent;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static com.intellij.openapi.editor.ScrollType.MAKE_VISIBLE;

@RequiredArgsConstructor
public class FilePosition {
    private final File file;
    private final int lineNumber;

    public static FilePosition positionFromFileSource(FileBasedComponent source) {
        return new FilePosition(
                source.getSource(),
                source.getLineNumber()
        );
    }

    public void moveToPosition(PluginContext context) {
        context.navigateTo(file);
        moveToLine(context.getProject());
    }

    private void moveToLine(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        editor.getCaretModel().moveToVisualPosition(new VisualPosition(lineNumber, 0));
        editor.getScrollingModel().scrollToCaret(MAKE_VISIBLE);
    }
}