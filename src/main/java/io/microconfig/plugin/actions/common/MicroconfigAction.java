package io.microconfig.plugin.actions.common;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.microconfig.plugin.ActionHandler;
import io.microconfig.plugin.PluginContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MicroconfigAction extends AnAction {
    private final HandlerFactory factory;

    @Override
    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            factory.getHandler(context).ifPresent(ActionHandler::onAction);
        } catch (RuntimeException e) {
            context.showErrorHing(e);
        }
    }
}