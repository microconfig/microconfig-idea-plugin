package io.microconfig.plugin.actions.jump;

import io.microconfig.configs.provider.Include;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.MicroconfigAction;
import io.microconfig.plugin.actions.common.PluginContext;

import java.io.File;

import static io.microconfig.factory.MicroconfigFactory.ENV_DIR;
import static io.microconfig.plugin.actions.resolve.PlaceholderBorders.borders;

public class JumpAction extends MicroconfigAction {
    @Override
    protected ActionHandler chooseHandler(PluginContext context) {
        String currentLine = context.currentLine();

        if (Include.isInclude(currentLine)) {
            return new JumpToInclude();
        }
        if (borders(currentLine, context.currentColumn()).isInsidePlaceholder()) {
            return new JumpToPlaceholder();
        }
        if (isEnvDescriptor(context.currentFile())) {
            return new JumpFromEnv();
        }

        return null;
    }

    private boolean isEnvDescriptor(File currentFile) {
        return (currentFile.getName().endsWith(".yaml")
                || currentFile.getName().endsWith(".json"))
                && currentFile.getParentFile()
                .getAbsolutePath()
                .replace('\\', '/')
                .contains(ENV_DIR);
    }
}