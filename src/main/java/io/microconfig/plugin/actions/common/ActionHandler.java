package io.microconfig.plugin.actions.common;

import io.microconfig.plugin.microconfig.MicroconfigApi;

public interface ActionHandler {
    void onAction(PluginContext context, MicroconfigApi api);
}