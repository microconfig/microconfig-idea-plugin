package io.microconfig.plugin.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import lombok.Getter;
import lombok.Setter;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static io.microconfig.plugin.run.RunnerState.*;

public class RunConfig extends RunConfigurationBase implements RunnerSettings {
    private static final String MICROCONFIG_RUN_CONFIG = "MicroconfigRunConfig";
    private final RunConfigEditor editor;

    @Getter
    @Setter
    private String env = "dev";

    @Getter
    @Setter
    private String groups = "";

    @Getter
    @Setter
    private String services = "";

    @Getter
    @Setter
    private String destination = "";

    public RunConfig(RunConfigFactory factory, Project project) {
        super(project, factory, "Generate " + project.getName());
        this.editor = new RunConfigEditor();
        this.destination = new File(project.getBasePath(), "/build/configs/").getAbsolutePath();
    }

    @NotNull
    @Override
    public SettingsEditor<RunConfig> getConfigurationEditor() {
        return editor;
    }

    @Override
    public void checkConfiguration() {
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        Element runConfig = new Element(MICROCONFIG_RUN_CONFIG);
        runConfig.setAttribute(ENV, valueOrEmpty(env));
        runConfig.setAttribute(GROUPS, valueOrEmpty(groups));
        runConfig.setAttribute(SERVICES, valueOrEmpty(services));
        runConfig.setAttribute(DESTINATION, valueOrEmpty(destination));

        element.addContent(runConfig);
        super.writeExternal(element);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        Element runConfig = element.getChild(MICROCONFIG_RUN_CONFIG);
        if (runConfig == null) return;

        this.env = valueOrEmpty(runConfig.getAttributeValue(ENV));
        this.groups = valueOrEmpty(runConfig.getAttributeValue(GROUPS));
        this.services = valueOrEmpty(runConfig.getAttributeValue(SERVICES));
        this.destination = valueOrEmpty(runConfig.getAttributeValue(DESTINATION));
        editor.resetEditorFrom(this);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new RunnerState(environment, this);
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }
}
