package io.microconfig.plugin.run;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.options.SettingsEditor;
import io.microconfig.commands.Command;
import io.microconfig.commands.buildconfig.entry.BuildConfigMain;
import io.microconfig.plugin.microconfig.MicroconfigInitializerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static io.microconfig.plugin.run.MicroconfigRunConfigurationType.ID;
import static io.microconfig.plugin.utils.FileUtil.toFile;

public class MicroconfigRunner implements ProgramRunner<MicroconfigRunConfiguration> {

    @NotNull
    @Override
    public String getRunnerId() {
        return ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals("Run");
    }

    @Nullable
    @Override
    public MicroconfigRunConfiguration createConfigurationData(ConfigurationInfoProvider settingsProvider) {
        return (MicroconfigRunConfiguration) settingsProvider.getConfiguration();
    }

    @Override
    public void checkConfiguration(RunnerSettings settings, @Nullable ConfigurationPerRunnerSettings configurationPerRunnerSettings) throws RuntimeConfigurationException {
        MicroconfigRunSettings microSettings = (MicroconfigRunSettings) settings;
        //todo if needed
    }

    @Override
    public void onProcessStarted(RunnerSettings settings, ExecutionResult executionResult) {
        //do nothing
    }

    @Nullable
    @Override
    public SettingsEditor<MicroconfigRunConfiguration> getSettingsEditor(Executor executor, RunConfiguration configuration) {
        return MicroconfigRunConfiguration.class.cast(configuration).getConfigurationEditor(); //todo tricky place, if null or current no editor visible
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment) {
        MicroconfigRunConfiguration c = (MicroconfigRunConfiguration) environment.getRunnerAndConfigurationSettings().getConfiguration();
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
        //todo
    }

}
