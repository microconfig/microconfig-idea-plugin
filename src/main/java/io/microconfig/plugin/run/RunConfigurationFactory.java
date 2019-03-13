package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RunConfigurationFactory extends ConfigurationFactory {

    private final MicroconfigGenerateSettingsEditor editor = new MicroconfigGenerateSettingsEditor();

    public RunConfigurationFactory(RunConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new MicroconfigRunConfiguration(this, editor, project);
    }

}
