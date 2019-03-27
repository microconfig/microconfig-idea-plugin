package io.microconfig.plugin.actions.handler;

import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.PluginContext;

public interface ActionHandler {
    void onAction(PluginContext context, MicroconfigApi api);
}