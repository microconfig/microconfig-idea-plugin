package io.microconfig.plugin.jumps;

import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import static io.microconfig.plugin.utils.ContextUtils.moveToLineColumn;
import static io.microconfig.plugin.utils.FileUtil.toPsiFile;
import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;
import static io.microconfig.plugin.utils.PlaceholderUtils.insidePlaceholderBrackets;
import static io.microconfig.plugin.utils.PlaceholderUtils.placeholderSubstring;

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
        String placeholder = placeholderSubstring(currentLine, context.caret.getLogicalPosition().column);
        if (!api.navigatable(placeholder)) return;

        FilePosition filePosition = api.findPlaceholderKey(context.projectDir(), placeholder, context.editorFile.getName());
        VirtualFile virtualFile = toVirtualFile(filePosition.getFile());
        PsiFile psiFile = toPsiFile(context.project, virtualFile);
        psiFile.navigate(true);
        moveToLineColumn(context.project, filePosition.getLineNumber(), 0);
    }
}
