package io.microconfig.plugin.jumps;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.microconfig.plugin.utils.ContextUtils.moveToLineColumn;
import static io.microconfig.plugin.utils.PlaceholderUtils.placeholderSubstring;
import static io.microconfig.plugin.utils.VirtialFileUtil.toPsiFile;
import static io.microconfig.plugin.utils.VirtialFileUtil.toVirtualFile;

@RequiredArgsConstructor
public class JumpToPlaceholder implements MicroconfigComponent {
    private final MicroconfigApi api;
    private final PluginContext context;
    private final String currentLine;

    @Override
    public void react() {
        Optional<String> placeholder = placeholderSubstring(currentLine, context.currentColumn());
        if (!placeholder.isPresent() || !api.navigatable(placeholder.get())) return; //todo maybe print a warning

        FilePosition filePosition = api.findPlaceholderSource(placeholder.get(), context.currentFile(), context.projectDir());
        System.out.println("Resolved file position " + filePosition);
        VirtualFile virtualFile = toVirtualFile(filePosition.getFile());
        PsiFile psiFile = toPsiFile(context.getProject(), virtualFile);
        psiFile.navigate(true);
        moveToLineColumn(context.getProject(), filePosition.getLineNumber(), 0);
    }
}
