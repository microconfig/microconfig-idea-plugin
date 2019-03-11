package io.microconfig.plugin.actions.jump;

import io.microconfig.configs.provider.Include;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.ActionHandlerFactory;
import io.microconfig.plugin.actions.common.PluginContext;

import java.util.Optional;

import static io.microconfig.plugin.actions.resolve.PlaceholderBorders.borders;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class JumpHandlerFactory implements ActionHandlerFactory {
    @Override
    public Optional<ActionHandler> getHandler(PluginContext context) {
        String currentLine = context.currentLine();
        if (Include.isInclude(currentLine)) {
            return of(new JumpToInclude());
        }

        if (borders(currentLine, context.currentColumn()).isInsidePlaceholder()) {
            return of(new JumpToPlaceholder());
        }

        return empty();
    }
}
