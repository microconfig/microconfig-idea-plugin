package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.icons.AllIcons;

public class RunConfigType extends ConfigurationTypeBase {

    public static final String ID = "RunConfigType";
    private final RunConfigFactory factory;

    public RunConfigType() {
        super(ID, "Microconfig", "Generate Config", AllIcons.General.Gear);
        factory = new RunConfigFactory(this);
        addFactory(factory);
    }

}