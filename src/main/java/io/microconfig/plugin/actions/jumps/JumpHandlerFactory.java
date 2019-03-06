package io.microconfig.plugin.actions.jumps;

import io.microconfig.configs.provider.Include;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.ActionHandlerFactory;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigApiImpl;

import java.util.Optional;

import static io.microconfig.plugin.actions.placeholders.PlaceholderBorders.borders;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class JumpHandlerFactory implements ActionHandlerFactory {
    private MicroconfigApi api = new MicroconfigApiImpl();

    @Override
    public Optional<ActionHandler> getHandler(PluginContext context) {
        String currentLine = context.currentLine();
        if (Include.isInclude(currentLine)) {
            return of(new JumpToInclude(api, context));
        }

        if (borders(currentLine, context.currentColumn()).isInsidePlaceholder()) {
            return of(new JumpToPlaceholder(api, context));
        }

        return empty();
    }
}
