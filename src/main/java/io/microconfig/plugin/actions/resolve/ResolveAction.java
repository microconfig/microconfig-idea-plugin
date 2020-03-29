package io.microconfig.plugin.actions.resolve;

import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.actions.handler.MicroconfigAction;
import io.microconfig.plugin.microconfig.PluginContext;

import static io.microconfig.plugin.actions.resolve.PlaceholderBorder.borders;

public class ResolveAction extends MicroconfigAction {
    @Override
    protected ActionHandler chooseHandler(PluginContext context) {
        PlaceholderBorder borders = borders(context.currentLine(), context.currentColumn());
        if (borders.isInsidePlaceholder()) {
            return new ResolvePlaceholder(borders.value());
        }

        return new ResolveFullLine();
    }
}