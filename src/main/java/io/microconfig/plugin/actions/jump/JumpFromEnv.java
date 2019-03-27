package io.microconfig.plugin.actions.jump;

import io.microconfig.environments.Component;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.actions.common.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class JumpFromEnv implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        String component = context.currentToken();
        if (component.isEmpty()) return;

        String env = envName(context);
        api.getMicroconfigInitializer()
                .getMicroconfigFactory(context.projectDir())
                .getEnvironmentProvider()
                .getByName(env)
                .getAllComponents()
                .stream()
                .filter(nameOrTypeEquals(component))
                .findFirst()
                .map(c -> findAnyComponentFile(c, env, context, api))
                .ifPresent(context::navigateTo);
    }

    private String envName(PluginContext context) {
        String name = context.currentFile().getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    private Predicate<Component> nameOrTypeEquals(String componentName) {
        return c -> c.getType().equals(componentName) || c.getName().equals(componentName);
    }

    private File findAnyComponentFile(Component c, String env, PluginContext context, MicroconfigApi api) {
        return api.findAnyComponentFile(c.getType(), env, context.projectDir());
    }
}