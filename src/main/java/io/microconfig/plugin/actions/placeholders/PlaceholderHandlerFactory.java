package io.microconfig.plugin.actions.placeholders;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.ActionHandlerFactory;
import io.microconfig.plugin.actions.common.PluginContext;

import java.util.Optional;

import static io.microconfig.configs.resolver.placeholder.Placeholder.placeholderMatcher;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class PlaceholderHandlerFactory implements ActionHandlerFactory {
    @Override
    public Optional<ActionHandler> getHandler(PluginContext context) {
        String currentLine = context.currentLine();
        if (!placeholderMatcher(currentLine).find()) return empty();

        PlaceholderBorders borders = PlaceholderBorders.borders(currentLine, context.currentColumn());
        if (borders.isInsidePlaceholder()) {
            return of(new ResolveOnePlaceholder(borders.value()));
        }

        return of(new ResolvePropertyLine());
    }
}