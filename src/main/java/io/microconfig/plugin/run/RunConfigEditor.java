package io.microconfig.plugin.run;

import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

public class RunConfigEditor extends SettingsEditor<RunConfig> {
    private final MicroconfigRunConfigPanel editorPanel = new MicroconfigRunConfigPanel();

    @NotNull
    @Override
    protected JComponent createEditor() {
        return editorPanel;
    }

    @Override
    protected void resetEditorFrom(@NotNull RunConfig config) {
        this.editorPanel.applyConfig(config);
    }

    @Override
    protected void applyEditorTo(@NotNull RunConfig config)  {
        this.editorPanel.updateConfig(config);
    }

    class MicroconfigRunConfigPanel extends JPanel {
        private final JTextField envText;
        private final JTextField groupText;
        private final JTextField servicesText;
        private final JTextField destinationText;

        MicroconfigRunConfigPanel() {
            super();
            GroupLayout layout = new GroupLayout(this);
            setLayout(layout);

            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            JLabel envLabel = new JLabel("Environment:");
            envText = new JTextField(40);

            JLabel groupsLabel = new JLabel("Groups:");
            groupText = new JTextField(40);

            JLabel servicesLabel = new JLabel("Services:");
            servicesText = new JTextField(40);

            JLabel destinationDir = new JLabel("Destination:");
            destinationText = new JTextField(40);

            layout.setHorizontalGroup(
                    layout.createParallelGroup(LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(envLabel)
                                    .addComponent(envText))
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(groupsLabel)
                                    .addComponent(groupText))
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(servicesLabel)
                                    .addComponent(servicesText))
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(destinationDir)
                                    .addComponent(destinationText))
            );

            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(BASELINE)
                            .addComponent(envLabel)
                            .addComponent(envText))
                    .addGroup(layout.createParallelGroup(BASELINE)
                            .addComponent(groupsLabel)
                            .addComponent(groupText))
                    .addGroup(layout.createParallelGroup(BASELINE)
                            .addComponent(servicesLabel)
                            .addComponent(servicesText))
                    .addGroup(layout.createParallelGroup(BASELINE)
                            .addComponent(destinationDir)
                            .addComponent(destinationText))
            );
        }

        void applyConfig(RunConfig config) {
            envText.setText(config.getEnv());
            groupText.setText(config.getGroups());
            servicesText.setText(config.getServices());
            destinationText.setText(config.getDestination());
        }

        void updateConfig(RunConfig config) {
            config.setEnv(envText.getText());
            config.setGroups(groupText.getText());
            config.setServices(servicesText.getText());
            config.setDestination(destinationText.getText());
        }
    }
}
