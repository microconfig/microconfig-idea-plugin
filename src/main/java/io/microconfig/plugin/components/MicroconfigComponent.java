package io.microconfig.plugin.components;

import io.microconfig.plugin.PluginContext;

public interface MicroconfigComponent {

    void consume(PluginContext context);

}
