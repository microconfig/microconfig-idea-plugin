package io.microconfig.plugin.jumps;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.microconfig.plugin.utils.ContextUtils.moveToLineColumn;
import static io.microconfig.plugin.utils.PlaceholderUtils.insidePlaceholderBrackets;
import static io.microconfig.plugin.utils.PlaceholderUtils.placeholderSubstring;
import static io.microconfig.plugin.utils.VirtialFileUtil.*;

@RequiredArgsConstructor
public class JumpToPlaceholder implements MicroconfigComponent {
    private final MicroconfigApi api;
    private final PluginContext context;
    private final String currentLine;

    static boolean insidePlaceholder(String currentLine, Caret caret) {
        return insidePlaceholderBrackets(currentLine, caret.getLogicalPosition().column);
    }

    @Override
    public void react() {
        Optional<String> placeholder = placeholderSubstring(currentLine, context.caret.getLogicalPosition().column);
        if (!placeholder.isPresent() || !api.navigatable(placeholder.get())) return; //todo maybe print a warning

        FilePosition filePosition = api.findPlaceholderSource(placeholder.get(), toFile(context.editorFile), context.projectDir());
        System.out.println("Resolved file position " + filePosition);
        VirtualFile virtualFile = toVirtualFile(filePosition.getFile());
        PsiFile psiFile = toPsiFile(context.project, virtualFile);
        psiFile.navigate(true);
        moveToLineColumn(context.project, filePosition.getLineNumber(), 0);
    }
}
