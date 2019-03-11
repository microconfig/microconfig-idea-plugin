package io.microconfig.plugin.actions.resolve;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.plugin.actions.resolve.PlaceholderUtils.printValues;

@RequiredArgsConstructor
public class ResolvePlaceholder implements ActionHandler {
    private final String value;

    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        //todo error hint if special placeholder
        Map<String, String> values = api.resolvePlaceholderForEachEnv(value, context.currentFile(), context.projectDir());
        printValues(value, values, context);
    }
}
