package io.microconfig.plugin.jumps;

import io.microconfig.configs.provider.Include;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigApiImpl;

import java.util.Optional;

import static io.microconfig.plugin.jumps.JumpToPlaceholder.insidePlaceholder;
import static io.microconfig.plugin.utils.ContextUtils.currentLine;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Jumps {
    private MicroconfigApi api = new MicroconfigApiImpl();

    public Optional<MicroconfigComponent> componentFrom(PluginContext context) {
        String currentLine = currentLine(context);
        if (Include.isInclude(currentLine)) {
            return of(new JumpToInclude(api, context, currentLine));
        }

        if (insidePlaceholder(currentLine, context.caret)) {
            return of(new JumpToPlaceholder(api, context, currentLine));
        }

        return empty();
    }
}
