package io.microconfig.plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathUtil;
import io.microconfig.commands.buildconfig.entry.BuildConfigMain;

public class RunnerState extends JavaCommandLineState {

    private final RunConfig configuration;

    protected RunnerState(ExecutionEnvironment environment, RunConfig configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters javaParams = new JavaParameters();

        String jarPath = PathUtil.getJarPathForClass(BuildConfigMain.class);
        javaParams.getClassPath().add(jarPath);

        Project project = getEnvironment().getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());
        javaParams.setMainClass(BuildConfigMain.class.getName());

        javaParams.getProgramParametersList().add("root=" + project.getBasePath());
        javaParams.getProgramParametersList().add("env=" + configuration.getEnv());
        javaParams.getProgramParametersList().add("groups=" + configuration.getGroups());
        javaParams.getProgramParametersList().add("services=" + configuration.getServices());
        javaParams.getProgramParametersList().add("dest=" + configuration.getDestination());

        return javaParams;
    }
}
