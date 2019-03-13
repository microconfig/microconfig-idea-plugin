package io.microconfig.plugin.actions.jump;

import io.microconfig.environments.Component;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class JumpFromEnv implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String env = envName(context);

        api.getMicroconfigInitializer()
                .getMicroconfigFactory(context.projectDir())
                .getEnvironmentProvider()
                .getByName(env)
                .getComponentByName(componentName(context))
                .map(c -> findAnyComponentFile(c, env, context, api))
                .ifPresent(context::navigateTo);
    }

    private String envName(PluginContext context) {
        String name = context.currentFile().getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    private String componentName(PluginContext context) {
        return context.currentToken();
    }

    private File findAnyComponentFile(Component c, String env, PluginContext context, MicroconfigApi api) {
        return api.findAnyComponentFile(c.getType(), env, context.projectDir());
    }
}