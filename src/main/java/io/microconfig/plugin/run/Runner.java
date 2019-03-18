package io.microconfig.plugin.run;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;

public class Runner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "Runner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals("Run") && profile instanceof RunConfig;
    }
}
