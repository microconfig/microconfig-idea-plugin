package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class RunConfigFactory extends ConfigurationFactory {
    public RunConfigFactory(RunConfigType type) {
        super(type);
    }


    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new RunConfig(this, project);
    }
}