package io.microconfig.plugin.actions.resolve;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.ActionHandlerFactory;
import io.microconfig.plugin.actions.common.PluginContext;

import java.util.Optional;

import static java.util.Optional.of;

class ResolveHandleFactory implements ActionHandlerFactory {
    @Override
    public Optional<ActionHandler> getHandler(PluginContext context) {
        PlaceholderBorders borders = PlaceholderBorders.borders(context.currentLine(), context.currentColumn());
        if (borders.isInsidePlaceholder()) {
            return of(new ResolvePlaceholder(borders.value()));
        }

        return of(new ResolveFullLine());
    }
}