package io.microconfig.plugin.actions.jump;

import io.microconfig.environments.Component;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JumpFromEnv implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        api.getMicroconfigInitializer()
                .getMicroconfigFactory(context.projectDir())
                .getEnvironmentProvider()
                .getByName(envName(context))
                .getComponentByName(componentName(context))
                .ifPresent(c-> navigateTo(c, context));
    }

    private String envName(PluginContext context) {
        String name = context.currentFile().getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    private String componentName(PluginContext context) {
        return null;
    }

    private void navigateTo(Component component, PluginContext context) {

    }
}