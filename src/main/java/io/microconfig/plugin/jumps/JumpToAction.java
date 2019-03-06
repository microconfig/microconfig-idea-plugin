package io.microconfig.plugin.jumps;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;

public class JumpToAction extends AnAction {
    private final Jumps factory = new Jumps();

    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            factory.componentFrom(context).ifPresent(MicroconfigComponent::react);
        } catch (RuntimeException e) {
            context.showErrorHing(e);
        }
    }
}