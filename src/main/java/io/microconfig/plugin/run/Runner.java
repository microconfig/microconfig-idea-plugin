package io.microconfig.plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;

import static com.intellij.execution.runners.DefaultProgramRunnerKt.executeState;

public class Runner extends GenericProgramRunner<RunnerSettings> {
    @Override
    @NotNull
    public String getRunnerId() {
        return "Runner";
    }

    @Override
    public boolean canRun(String executorId, @NotNull RunProfile profile) {
        return executorId.equals("Run") && profile instanceof RunConfig;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
        return executeState(state, env, this);
    }
}
