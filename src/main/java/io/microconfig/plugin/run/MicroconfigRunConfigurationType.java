package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.util.ui.ColorIcon;

import static com.intellij.ui.JBColor.BLUE;

public class MicroconfigRunConfigurationType extends ConfigurationTypeBase {
    private static final String ID = "12064";
    private final RunConfigurationFactory factory;

    public MicroconfigRunConfigurationType() {
        super(ID, "Microconfig", "Generate Config", new ColorIcon(10, 5, BLUE, true));
        factory = new RunConfigurationFactory(this);
        addFactory(factory);
    }
}