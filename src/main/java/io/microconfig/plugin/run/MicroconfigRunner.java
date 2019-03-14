package io.microconfig.plugin.run;

import com.intellij.execution.ExecutionException;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.microconfig.plugin.run.MicroconfigRunConfigurationType.ID;

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
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        MicroconfigRunConfiguration configuration = (MicroconfigRunConfiguration) environment.getRunnerAndConfigurationSettings().getConfiguration();
        System.out.println("Running with " + configuration.getName());
        //todo
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment environment, @Nullable Callback callback) throws ExecutionException {
        MicroconfigRunConfiguration configuration = (MicroconfigRunConfiguration) environment.getRunnerAndConfigurationSettings().getConfiguration();
        //todo
    }

}
