package io.microconfig.plugin.actions.jumps;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.FilePosition;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.actions.placeholders.PlaceholderBorders;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JumpToPlaceholder implements ActionHandler {
    private final MicroconfigApi api;
    private final PluginContext context;

    @Override
    public void onAction() {
        String placeholder = PlaceholderBorders.borders(context.currentLine(), context.currentColumn()).value();
        if (placeholder == null || !api.navigatable(placeholder)) return; //todo maybe print a warning

        FilePosition filePosition = api.findPlaceholderSource(placeholder, context.currentFile(), context.projectDir());
        filePosition.moveTo(context.getProject());
    }
}
