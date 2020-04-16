package io.microconfig.plugin.actions.jump;

import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.PluginContext;
import lombok.RequiredArgsConstructor;

import java.io.File;

import static io.microconfig.utils.FileUtils.getName;

@RequiredArgsConstructor
public class JumpFromEnv implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String component = context.currentToken(); //todo alias
        if (component.isEmpty()) return;

        String env = envName(context);
        File someComponentFile = api.findAnyComponentFile(component, env, context.projectDir());
        context.navigateTo(someComponentFile);
    }

    private String envName(PluginContext context) {
        return getName(context.currentFile());
    }
}