package io.microconfig.plugin.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MicroconfigGenerateSettingsEditor extends SettingsEditor<MicroconfigRunConfiguration> {

    private final JPanel editorPanel = new JPanel();
    private final JTextField env = new JTextField();
    private final JTextField group = new JTextField();
    private final JTextField services = new JTextField();

    public MicroconfigGenerateSettingsEditor() {
        new JLabel("Envs").setLabelFor(env);
        editorPanel.add(env);
        editorPanel.add(group);
        editorPanel.add(services);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return editorPanel;
    }

    @Override
    protected void resetEditorFrom(@NotNull MicroconfigRunConfiguration config) {
        this.env.setText(config.getEnvs());
        this.group.setText(config.getGroups());
        this.services.setText(config.getServices());
    }

    @Override
    protected void applyEditorTo(@NotNull MicroconfigRunConfiguration config) throws ConfigurationException {
        config.setEnvs(env.getText());
        config.setGroups(group.getText());
        config.setServices(services.getText());
    }

}
