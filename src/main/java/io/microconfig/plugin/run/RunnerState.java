package io.microconfig.plugin.run;

import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathUtil;
import io.microconfig.commands.buildconfig.entry.BuildConfigMain;
import io.microconfig.plugin.microconfig.MicroconfigInitializerImpl;

import java.io.File;
import java.util.function.BiConsumer;

import static io.microconfig.utils.StringUtils.isEmpty;

public class RunnerState extends JavaCommandLineState {
    private final RunConfig configuration;

    protected RunnerState(ExecutionEnvironment environment, RunConfig configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() {
        JavaParameters javaParams = new JavaParameters();

        Project project = getEnvironment().getProject();
        javaParams.setJdk(ProjectRootManager.getInstance(project).getProjectSdk());
        javaParams.getClassPath().add(PathUtil.getJarPathForClass(BuildConfigMain.class));
        javaParams.setMainClass(BuildConfigMain.class.getName());

        BiConsumer<String, String> addParam = (key, value) -> {
            if (isEmpty(value)) return;
            javaParams.getProgramParametersList().add(key + "=" + value);
        };

        addParam.accept("root", "\"" + new MicroconfigInitializerImpl().findConfigRootDir(new File(project.getBasePath())) + "\"");
        addParam.accept("env", trim(configuration.getEnv()));
        addParam.accept("groups", trim(configuration.getGroups()));
        addParam.accept("services", trim(configuration.getServices()));
        addParam.accept("dest", configuration.getDestination().trim());

        return javaParams;
    }

    private String trim(String param) {
        return param.replaceAll("\\s+", "");
    }
}
