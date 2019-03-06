package io.microconfig.plugin.actions.placeholders;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.plugin.actions.placeholders.PlaceholderUtils.printValues;

@RequiredArgsConstructor
public class ResolveOnePlaceholder implements ActionHandler {
    private final MicroconfigApi api;
    private final PluginContext context;
    private final String value;

    @Override
    public void onAction() {
        //todo error hint if special placeholder
        Map<String, String> values = api.resolveOnePlaceholderForEachEnv(value, context.currentFile(), context.projectDir());
        printValues(value, values, context);
    }
}
