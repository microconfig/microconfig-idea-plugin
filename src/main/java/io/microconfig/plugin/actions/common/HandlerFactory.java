package io.microconfig.plugin.actions.common;

import io.microconfig.plugin.ActionHandler;
import io.microconfig.plugin.PluginContext;

import java.util.Optional;

public interface HandlerFactory {
    Optional<ActionHandler> getHandler(PluginContext context);
}
