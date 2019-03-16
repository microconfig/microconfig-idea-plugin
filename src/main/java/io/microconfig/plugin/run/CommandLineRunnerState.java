package io.microconfig.plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import io.microconfig.commands.buildconfig.entry.BuildConfigMain;

public class CommandLineRunnerState extends JavaCommandLineState {

    private final MicroconfigRunConfiguration configuration;

    protected CommandLineRunnerState(ExecutionEnvironment environment, MicroconfigRunConfiguration configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters javaParams = new JavaParameters();
        Project project = getEnvironment().getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());
        javaParams.setMainClass(BuildConfigMain.class.getName());

        return javaParams;
    }
}
