package io.microconfig.plugin.jumps;

import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigApiMock;

import java.util.Optional;

import static io.microconfig.plugin.jumps.JumpToInclude.hasIncludeTag;
import static io.microconfig.plugin.jumps.JumpToPlaceholder.insidePlaceholder;
import static io.microconfig.plugin.utils.ContextUtils.currentLine;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class Jumps {
    private MicroconfigApi api = new MicroconfigApiMock();

    public Optional<MicroconfigComponent> componentFrom(PluginContext context) {
        String currentLine = currentLine(context);
        if (hasIncludeTag(currentLine)) {
            return of(new JumpToInclude(api, context, currentLine));
        }
        if (insidePlaceholder(api, currentLine, context.caret)) {
            return of(new JumpToPlaceholder(api, context, currentLine));
        }
        return empty();
    }
}