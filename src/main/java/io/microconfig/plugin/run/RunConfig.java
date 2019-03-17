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

public class RunConfig extends RunConfigurationBase implements RunnerSettings {
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
    private String destination = "full path";

    public RunConfig(RunConfigFactory factory, Project project) {
        super(project, factory, "Generate " + project.getName());
        this.editor = new RunConfigEditor();
        this.destination = project.getBasePath() + "/build/configs";
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
        super.writeExternal(element);

        Element runConfig = new Element("MicroconfigRunConfig");
        runConfig.setAttribute("env", env);
        runConfig.setAttribute("groups", groups);
        runConfig.setAttribute("services", services);
        runConfig.setAttribute("destination", destination);

        element.addContent(runConfig);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        Element runConfig = element.getChild("MicroconfigRunConfig");
        if (runConfig == null) return;

        this.env = runConfig.getAttributeValue("env");
        this.groups = runConfig.getAttributeValue("groups");
        this.services = runConfig.getAttributeValue("services");
        this.destination = runConfig.getAttributeValue("destination");
        editor.resetEditorFrom(this);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new RunnerState(environment, this);
    }
}
