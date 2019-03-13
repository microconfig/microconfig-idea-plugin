package io.microconfig.plugin.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MicroconfigGenerateSettingsEditor extends SettingsEditor<MicroconfigRunConfiguration> {

    private final MicroconfigRunPanel editorPanel = new MicroconfigRunPanel();

    @NotNull
    @Override
    protected JComponent createEditor() {
        return editorPanel;
    }

    @Override
    protected void resetEditorFrom(@NotNull MicroconfigRunConfiguration config) {
        this.editorPanel.applyConfig(config);
    }

    @Override
    protected void applyEditorTo(@NotNull MicroconfigRunConfiguration config) throws ConfigurationException {
        this.editorPanel.updateConfig(config);
    }

}
