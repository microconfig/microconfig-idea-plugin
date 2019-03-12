package io.microconfig.plugin.actions.common;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.microconfig.plugin.microconfig.MicroconfigApiImpl;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class MicroconfigAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            ActionHandler actionHandler = chooseHandler(context);
            if (actionHandler == null) return;
            actionHandler.onAction(context, new MicroconfigApiImpl());
        } catch (RuntimeException e) {
            context.showErrorHing(e);
        }
    }

    protected abstract ActionHandler chooseHandler(PluginContext context);
}