package io.microconfig.plugin.run;

import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ex.PathUtilEx;
import com.intellij.util.PathUtil;
import io.microconfig.commands.buildconfig.entry.BuildConfigMain;
import io.microconfig.plugin.microconfig.MicroconfigInitializerImpl;

import java.io.File;
import java.util.function.BiConsumer;

import static io.microconfig.utils.StringUtils.isEmpty;

public class RunnerState extends JavaCommandLineState {
    static final String ROOT = "root";
    static final String ENV = "env";
    static final String GROUPS = "groups";
    static final String SERVICES = "services";
    static final String DESTINATION = "dest";

    private final RunConfig configuration;

    protected RunnerState(ExecutionEnvironment environment, RunConfig configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @Override
    protected JavaParameters createJavaParameters() {
        JavaParameters javaParams = new JavaParameters();

        Project project = getEnvironment().getProject();

        javaParams.setJdk(PathUtilEx.getAnyJdk(project));
        javaParams.getClassPath().add(PathUtil.getJarPathForClass(BuildConfigMain.class));
        javaParams.setMainClass(BuildConfigMain.class.getName());

        BiConsumer<String, String> addParam = (key, value) -> {
            if (isEmpty(value)) return;
            javaParams.getProgramParametersList().add(key + "=" + value);
        };

        addParam.accept(ROOT, escapeParam(new MicroconfigInitializerImpl().findConfigRootDir(new File(project.getBasePath())).getAbsolutePath()));
        addParam.accept(ENV, trim(configuration.getEnv()));
        addParam.accept(GROUPS, trim(configuration.getGroups()));
        addParam.accept(SERVICES, trim(configuration.getServices()));
        addParam.accept(DESTINATION, escapeParam(configuration.getDestination()));

        return javaParams;
    }

    private String escapeParam(String param) {
        return "\"" + param + "\"";
    }

    private String trim(String param) {
        return param.replaceAll("\\s+", "");
    }
}
