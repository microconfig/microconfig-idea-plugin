package io.microconfig.plugin.placeholders;

import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigApiImpl;

import java.util.Optional;

import static io.microconfig.plugin.utils.PlaceholderUtils.hasPlaceholder;
import static io.microconfig.plugin.utils.PlaceholderUtils.insidePlaceholderBrackets;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Placeholders {
    private final MicroconfigApi api = new MicroconfigApiImpl();

    public Optional<MicroconfigComponent> componentFrom(PluginContext context) {
        String currentLine = context.currentLine();
        if (!hasPlaceholder(currentLine)) return empty();

        if (insidePlaceholderBrackets(currentLine, context.currentColumn())) {
            return of(new ResolvePlaceholderValue(api, context, currentLine));
        }

        return of(new ResolvePlaceholderLine(api, context, currentLine));
    }
}