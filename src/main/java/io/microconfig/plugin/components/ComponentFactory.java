package io.microconfig.plugin.components;

import io.microconfig.plugin.PluginContext;

import java.util.Optional;

import static io.microconfig.plugin.ContextUtils.currentLine;
import static io.microconfig.plugin.components.IncludeComponent.hasIncludeTag;

public class ComponentFactory {

    public Optional<MicroconfigComponent> componentFrom(PluginContext context) {
        String currentLine = currentLine(context);
        if (hasIncludeTag(currentLine)) return Optional.of(new IncludeComponent(context, currentLine));
        return Optional.empty();
    }

}
