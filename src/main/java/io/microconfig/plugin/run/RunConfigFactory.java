package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RunConfigFactory extends ConfigurationFactory {
    public RunConfigFactory(RunConfigType type) {
        super(type);
    }

    @Override
    @NotNull
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new RunConfig(this, project);
    }

    @Override
    public RunConfiguration createConfiguration(String name, RunConfiguration template) {
        RunConfig newConfiguration = (RunConfig) template.clone();
        newConfiguration.setName(name);
        newConfiguration.setEditor(new RunConfigEditor());
        return newConfiguration;
    }
}