package io.microconfig.plugin.actions.resolve;

import io.microconfig.configs.Property;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.configs.sources.FileSource.fileSource;
import static io.microconfig.plugin.actions.resolve.Hints.showHint;

@RequiredArgsConstructor
public class ResolveFullLine implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String currentLine = context.currentLine();
        Map<String, String> values = api.resolveFullLineForEachEnv(currentLine, context.currentFile(), context.projectDir());

        String key = Property.parse(currentLine, "", fileSource(context.currentFile(), 0, false)).getKey();
        showHint(key, values, context);
    }
}