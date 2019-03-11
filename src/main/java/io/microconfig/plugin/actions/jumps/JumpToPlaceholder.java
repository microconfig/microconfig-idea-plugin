package io.microconfig.plugin.actions.jumps;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.FilePosition;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.actions.placeholders.PlaceholderBorders;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JumpToPlaceholder implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String placeholder = PlaceholderBorders.borders(context.currentLine(), context.currentColumn()).value();
        if (placeholder == null || !api.navigatable(placeholder)) return; //todo maybe print a warning

        FilePosition position = api.findPlaceholderSource(placeholder, context.currentFile(), context.projectDir());
        position.moveToPosition(context.getProject());
    }
}
