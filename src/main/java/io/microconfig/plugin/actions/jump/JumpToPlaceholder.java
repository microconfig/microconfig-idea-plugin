package io.microconfig.plugin.actions.jump;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.actions.common.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import static io.microconfig.plugin.actions.resolve.PlaceholderBorders.borders;

@RequiredArgsConstructor
public class JumpToPlaceholder implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String placeholder = borders(context.currentLine(), context.currentColumn()).value();
        if (placeholder == null) return;

        api.findPlaceholderSource(placeholder, context.currentFile(), context.projectDir())
                .moveToPosition(context);
    }
}