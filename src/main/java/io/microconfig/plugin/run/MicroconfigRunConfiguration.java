package io.microconfig.plugin.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MicroconfigRunConfiguration extends RunConfigurationBase {
    private final Project project;
    private final MicroconfigGenerateSettingsEditor editor;

    @Getter
    @Setter
    private String envs = "Envs";

    @Getter
    @Setter
    private String groups = "Groups";

    @Getter
    @Setter
    private String services = "Services";

    public MicroconfigRunConfiguration(RunConfigurationFactory factory, MicroconfigGenerateSettingsEditor editor, Project project) {
        super(project, factory, "Generate " + project.getName());
        this.project = project;
        this.editor = editor;
    }

    @NotNull
    @Override
    public SettingsEditor<MicroconfigRunConfiguration> getConfigurationEditor() {
        return editor;
    }

    @Override
    public void checkConfiguration() {
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return null;
    }
}
