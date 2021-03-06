package io.microconfig.plugin.actions.resolve;

import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.PluginContext;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.plugin.actions.resolve.Hints.showHint;

@RequiredArgsConstructor
public class ResolvePlaceholder implements ActionHandler {
    private final String value;

    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        Map<String, String> values = api.resolvePlaceholderForEachEnv(value, context.currentFile(), context.projectDir());
        showHint(value, values, context);
    }
}
