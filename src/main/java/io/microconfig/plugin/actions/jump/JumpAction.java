package io.microconfig.plugin.actions.jump;

import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.actions.handler.MicroconfigAction;
import io.microconfig.plugin.microconfig.PluginContext;
import io.microconfig.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static io.microconfig.core.environments.repository.FileEnvironmentRepository.ENV_DIR;
import static io.microconfig.core.properties.repository.Includes.isInclude;
import static io.microconfig.plugin.actions.resolve.PlaceholderBorder.borders;
import static io.microconfig.utils.FileUtils.getExtension;
import static java.util.Arrays.asList;

public class JumpAction extends MicroconfigAction {
    @Override
    protected ActionHandler chooseHandler(PluginContext context) {
        String currentLine = context.currentLine();

        if (isInclude(currentLine)) {
            return new JumpToInclude();
        }
        if (isInsidePlaceholder(context, currentLine)) {
            return new JumpToPlaceholder();
        }
        if (isEnvDescriptor(context.currentFile())) {
            return new JumpFromEnv();
        }

        return null;
    }

    private boolean isInsidePlaceholder(PluginContext context, String currentLine) {
        return borders(currentLine, context.currentColumn()).isInsidePlaceholder();
    }

    private boolean isEnvDescriptor(File currentFile) {
        return asList(".yaml", ".json").contains(getExtension(currentFile))
                && parentOf(currentFile).contains('/' + ENV_DIR + '/');
    }

    private String parentOf(File currentFile) {
        return currentFile.getParentFile().getAbsolutePath().replace('\\', '/');
    }
}