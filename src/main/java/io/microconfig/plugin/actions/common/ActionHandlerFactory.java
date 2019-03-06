package io.microconfig.plugin.actions.common;

import java.util.Optional;

public interface ActionHandlerFactory {
    Optional<ActionHandler> getHandler(PluginContext context);
}
