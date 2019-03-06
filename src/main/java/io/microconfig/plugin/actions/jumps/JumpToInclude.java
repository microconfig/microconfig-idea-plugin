package io.microconfig.plugin.actions.jumps;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.microconfig.plugin.ActionHandler;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.plugin.utils.FileUtil.toPsiFile;
import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;

@RequiredArgsConstructor
public class JumpToInclude implements ActionHandler {
    private final MicroconfigApi api;
    private final PluginContext context;

    @Override
    public void onAction() {
        File componentFile = api.findIncludeSource(context.currentLine(), context.currentFile(), context.projectDir());
        VirtualFile virtualFile = toVirtualFile(componentFile);
        PsiFile psiFile = toPsiFile(context.getProject(), virtualFile);
        psiFile.navigate(true);
    }
}
