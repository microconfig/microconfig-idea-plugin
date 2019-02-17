package io.microconfig.plugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import static com.intellij.openapi.actionSystem.CommonDataKeys.CARET;
import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE;

class PluginContext {
    final Project project;
    final Editor editor;
    final Caret caret;
    final VirtualFile editorFile;

    PluginContext(AnActionEvent event) {
        project = event.getProject();
        editor = event.getData(EDITOR);
        caret = event.getData(CARET);
        editorFile = event.getData(VIRTUAL_FILE);
    }

    boolean notFull() {
        return project == null || editor == null || editorFile == null || caret == null;
    }
}
