package io.microconfig.plugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

import static com.intellij.openapi.actionSystem.CommonDataKeys.*;
import static io.microconfig.plugin.utils.FileUtil.toFile;

public class PluginContext {
    public final Project project;
    public final Editor editor;
    public final Caret caret;
    public final VirtualFile editorFile;

    public PluginContext(AnActionEvent event) {
        project = event.getProject();
        editor = event.getData(EDITOR);
        caret = event.getData(CARET);
        editorFile = event.getData(VIRTUAL_FILE);
    }

    public boolean notFull() {
        return project == null || editor == null || editorFile == null || caret == null;
    }

    public File projectDir() {
        return toFile(project.getBaseDir());
    }
}