package io.microconfig.plugin.placeholders;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.PluginException;

public class ResolvePlaceholderAction extends AnAction {
    private final Placeholders factory = new Placeholders();

    @Override
    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            factory.componentFrom(context).ifPresent(MicroconfigComponent::react);
        } catch (PluginException e) {
            context.showErrorHing(e.getMessage());
        }
    }
}
