package io.microconfig.plugin.actions.jump;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.FilePosition;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.actions.resolve.PlaceholderBorders;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JumpToPlaceholder implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String placeholder = PlaceholderBorders.borders(context.currentLine(), context.currentColumn()).value();
        if (placeholder == null) return;

        FilePosition position = api.findPlaceholderSource(placeholder, context.currentFile(), context.projectDir());
        position.moveToPosition(context.getProject());
    }
}
