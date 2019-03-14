package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;

public class MicroconfigRunConfigurationType extends ConfigurationTypeBase {

    public static final String ID = "MicroconfigRunConfigurationType";
    private final RunConfigurationFactory factory;

    public MicroconfigRunConfigurationType() {
        super(ID, "Microconfig", "Generate Config", AllIcons.General.Gear);
        factory = new RunConfigurationFactory(this);
        addFactory(factory);
    }

}