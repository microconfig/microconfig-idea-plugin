package io.microconfig.plugin;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.microconfig.plugin.components.ComponentFactory;
import io.microconfig.plugin.components.MicroconfigComponent;

public class ResolveAction extends AnAction {
    private final ComponentFactory factory = new ComponentFactory();

    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            factory.componentFrom(context).ifPresent(MicroconfigComponent::react);
        } catch (PluginException e) {
            HintManager.getInstance().showErrorHint(context.editor, e.getMessage());
        }
    }

}