package io.microconfig.plugin.jumps;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.plugin.utils.VirtialFileUtil.*;

@RequiredArgsConstructor
public class JumpToInclude implements MicroconfigComponent {
    private final MicroconfigApi api;
    private final PluginContext context;
    private final String currentLine;

    @Override
    public void react() {
        File componentFile = api.findIncludeSource(currentLine, toFile(context.editorFile), context.projectDir());
        VirtualFile virtualFile = toVirtualFile(componentFile);
        PsiFile psiFile = toPsiFile(context.project, virtualFile);
        psiFile.navigate(true);
    }
}
