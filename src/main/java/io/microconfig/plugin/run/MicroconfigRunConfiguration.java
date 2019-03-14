package io.microconfig.plugin.run;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.microconfig.utils.StringUtils.splitToList;

public class MicroconfigRunConfiguration extends RunConfigurationBase implements RunnerSettings {
    private final Project project;
    private final MicroconfigRunConfigEditor editor;

    @Getter
    @Setter
    private String env = "dev";

    @Getter
    @Setter
    private String groups = "group1,group2";

    @Getter
    @Setter
    private String services = "service1,service2";

    @Getter
    @Setter
    private String destination = "full path";

    public MicroconfigRunConfiguration(RunConfigurationFactory factory, Project project) {
        super(project, factory, "Generate " + project.getName());
        this.project = project;
        this.editor = new MicroconfigRunConfigEditor();
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

    public List<String> getGroupsAsList() {
        return splitToList(groups, ",");
    }

    public List<String> geComponentsAsList() {
        return splitToList(services, ",");
    }
}
