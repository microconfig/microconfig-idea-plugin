package io.microconfig.plugin.actions.jump;

import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.PluginContext;
import lombok.RequiredArgsConstructor;

import static io.microconfig.plugin.actions.resolve.PlaceholderBorder.borders;

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