package io.microconfig.plugin.run;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationInfoProvider;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.SettingsEditor;
import io.microconfig.commands.Command;
import io.microconfig.commands.buildconfig.entry.BuildConfigMain;
import io.microconfig.plugin.microconfig.MicroconfigInitializerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static io.microconfig.plugin.utils.FileUtil.toFile;

public class MicroconfigRunner implements ProgramRunner<MicroconfigRunConfiguration> {

    @NotNull
    @Override
    public String getRunnerId() {
        return "Run";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals("Run") && profile instanceof MicroconfigRunConfiguration;
    }

    @Nullable
    @Override
    public MicroconfigRunConfiguration createConfigurationData(ConfigurationInfoProvider settingsProvider) {
        return (MicroconfigRunConfiguration) settingsProvider.getConfiguration();
    }

    @Override
    public void checkConfiguration(RunnerSettings settings, @Nullable ConfigurationPerRunnerSettings configurationPerRunnerSettings) throws RuntimeConfigurationException {
        //todo if needed
        MicroconfigRunConfiguration configuration = (MicroconfigRunConfiguration) settings;
    }

    @Override
    public void onProcessStarted(RunnerSettings settings, ExecutionResult executionResult) {
        //do nothing
    }

    @Nullable
    @Override
    public SettingsEditor<MicroconfigRunConfiguration> getSettingsEditor(Executor executor, RunConfiguration configuration) {
        return null;
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) {
        MicroconfigRunConfiguration c = (MicroconfigRunConfiguration) environment
            .getRunnerAndConfigurationSettings()
            .getConfiguration();
        System.out.println("Running with " + c.getName());

        Command command = new MicroconfigInitializerImpl().newBuildCommand(
            toFile(environment.getProject().getBaseDir()),
            new File(c.getDestination())
        );
        BuildConfigMain.execute(command, c.getEnv(), c.getGroupsAsList(), c.geComponentsAsList());
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback) {
        execute(environment);
    }
}
