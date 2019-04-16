package io.microconfig.plugin.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.util.PathUtil;
import io.microconfig.entry.BuildConfigMain;
import io.microconfig.plugin.microconfig.impl.MicroconfigInitializerImpl;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.intellij.util.SystemProperties.getJavaHome;
import static io.microconfig.utils.StringUtils.isEmpty;

public class RunnerState extends CommandLineState {
    static final String ROOT = "root";
    static final String ENV = "env";
    static final String GROUPS = "groups";
    static final String SERVICES = "services";
    static final String DESTINATION = "dest";

    private final RunConfig configuration;

    public RunnerState(ExecutionEnvironment environment, RunConfig configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        OSProcessHandler processHandler = ProcessHandlerFactory
                .getInstance()
                .createColoredProcessHandler(new GeneralCommandLine(createJavaCmd()));

        ProcessTerminatedListener.attach(processHandler);
        return processHandler;

    }

    private List<String> createJavaCmd() {
        List<String> params = new ArrayList<>();

        String java = new File(getJavaHome(), "/bin/java").getAbsolutePath();
        String jarPath = PathUtil.getJarPathForClass(BuildConfigMain.class);

        params.add(java);
        params.add("-XX:TieredStopAtLevel=1");
        params.add("-jar");
        params.add(jarPath);

        BiConsumer<String, String> addParam = (key, value) -> {
            if (isEmpty(value)) return;
            params.add(key + "=" + value);
        };

        Project project = getEnvironment().getProject();
        addParam.accept(ROOT, escapeParam(new MicroconfigInitializerImpl().findConfigRootDir(new File(project.getBasePath())).getAbsolutePath()));
        addParam.accept(ENV, trim(configuration.getEnv()));
        addParam.accept(GROUPS, trim(configuration.getGroups()));
        addParam.accept(SERVICES, trim(configuration.getServices()));
        addParam.accept(DESTINATION, escapeParam(configuration.getDestination()));

        return params;
    }

    private String escapeParam(String param) {
        return isEmpty(param) ? "" : "\"" + param + "\"";
    }

    private String trim(String param) {
        return param.replaceAll("\\s+", "");
    }
}
