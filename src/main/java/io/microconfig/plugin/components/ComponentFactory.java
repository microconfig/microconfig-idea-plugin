package io.microconfig.plugin.components;

import io.microconfig.plugin.PluginContext;

import java.util.Optional;

import static io.microconfig.plugin.ContextUtils.currentLine;
import static io.microconfig.plugin.components.IncludeComponent.INCLUDE;

public class ComponentFactory {

    public Optional<MicroconfigComponent> componentFrom(PluginContext context) {
        String currentLine = currentLine(context);
        if (currentLine.startsWith(INCLUDE)) return Optional.of(new IncludeComponent(context, currentLine));
        return Optional.empty();
    }

}
