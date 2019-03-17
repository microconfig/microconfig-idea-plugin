package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;

public class RunConfigType extends ConfigurationTypeBase {
    public RunConfigType() {
        super("RunConfigType", "Microconfig", "Generate Config", AllIcons.General.Gear);
        addFactory(new RunConfigFactory(this));
    }
}