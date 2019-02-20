package io.microconfig.plugin.jumps;

import io.microconfig.plugin.MicroconfigApi;
import io.microconfig.plugin.MicroconfigApiMock;
import io.microconfig.plugin.MicroconfigComponent;
import io.microconfig.plugin.PluginContext;

import java.util.Optional;

import static io.microconfig.plugin.jumps.JumpToInclude.hasIncludeTag;
import static io.microconfig.plugin.utils.ContextUtils.currentLine;

public class Jumps {

    private MicroconfigApi api = new MicroconfigApiMock();

    public Optional<MicroconfigComponent> componentFrom(PluginContext context) {
        String currentLine = currentLine(context);
        if (hasIncludeTag(currentLine)) return Optional.of(new JumpToInclude(api, context, currentLine));
        return Optional.empty();
    }

}
