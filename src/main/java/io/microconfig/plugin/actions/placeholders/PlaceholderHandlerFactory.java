package io.microconfig.plugin.actions.placeholders;

import io.microconfig.plugin.ActionHandler;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.actions.common.HandlerFactory;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigApiImpl;

import java.util.Optional;

import static io.microconfig.configs.resolver.placeholder.Placeholder.placeholderMatcher;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class PlaceholderHandlerFactory implements HandlerFactory {
    private final MicroconfigApi api = new MicroconfigApiImpl();

    @Override
    public Optional<ActionHandler> getHandler(PluginContext context) {
        String currentLine = context.currentLine();
        if (!placeholderMatcher(currentLine).find()) return empty();

        PlaceholderBorders borders = PlaceholderBorders.borders(currentLine, context.currentColumn());
        if (borders.isInsidePlaceholder()) {
            return of(new ResolveOnePlaceholder(api, context, borders.value()));
        }

        return of(new ResolvePropertyLine(api, context));
    }
}