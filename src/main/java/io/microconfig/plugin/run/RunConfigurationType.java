package io.microconfig.plugin.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.util.ui.ColorIcon;

import java.awt.*;

public class RunConfigurationType extends ConfigurationTypeBase {

    private final RunConfigurationFactory factory;

    public RunConfigurationType() {
        super("12064", "Microconfig", "Generate Config", new ColorIcon(10, 5, Color.BLUE, true));
        factory = new RunConfigurationFactory(this);
        addFactory(factory);
    }

}
