package io.microconfig.plugin.run;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;

public class Runner extends DefaultProgramRunner {

    @Override
    public String getRunnerId() {
        return "Runner";
    }

    @Override
    public boolean canRun(String executorId, RunProfile profile) {
        return executorId.equals("Run") && profile instanceof RunConfig;
    }
}
