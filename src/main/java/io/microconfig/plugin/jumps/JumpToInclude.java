package io.microconfig.plugin.jumps;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.plugin.microconfig.MicroconfigApi.INCLUDE;
import static io.microconfig.plugin.utils.FileUtil.toPsiFile;
import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;

@RequiredArgsConstructor
public class JumpToInclude implements MicroconfigComponent {
    private final MicroconfigApi api;
    private final PluginContext context;
    private final String currentLine;

    static boolean hasIncludeTag(String currentLine) {
        return currentLine.startsWith(INCLUDE);
    }

    @Override
    public void react() {
        File componentFile = api.findInclude(context.projectDir(), currentLine, context.editorFile.getName());
        VirtualFile virtualFile = toVirtualFile(componentFile);
        PsiFile psiFile = toPsiFile(context.project, virtualFile);
        psiFile.navigate(true);
    }
}
