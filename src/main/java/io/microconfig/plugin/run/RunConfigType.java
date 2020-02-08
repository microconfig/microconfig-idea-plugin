package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.openapi.util.IconLoader;

public class RunConfigType extends ConfigurationTypeBase {
    public RunConfigType() {
        super("RunConfigType", "Microconfig", "Generate Config", IconLoader.getIcon("/logo.png"));
        addFactory(new RunConfigFactory(this));
    }
}