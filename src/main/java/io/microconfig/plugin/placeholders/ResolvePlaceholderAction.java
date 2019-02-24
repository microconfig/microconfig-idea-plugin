package io.microconfig.plugin.placeholders;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.PluginException;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigApiImpl;

import static io.microconfig.plugin.placeholders.ResolvePlaceholderLine.hasPlaceholder;
import static io.microconfig.plugin.utils.ContextUtils.currentLine;
import static io.microconfig.plugin.utils.ContextUtils.showErrorHing;

public class ResolvePlaceholderAction extends AnAction {
    private final MicroconfigApi api = new MicroconfigApiImpl();

    @Override
    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            String currentLine = currentLine(context);
            if (hasPlaceholder(currentLine)) {
                new ResolvePlaceholderLine(api, context, currentLine).react();
            }
        } catch (PluginException e) {
            showErrorHing(context.editor, e.getMessage());
        }
    }
}
