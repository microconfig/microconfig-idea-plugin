package io.microconfig.plugin.run;

import com.intellij.openapi.options.SettingsEditor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.RIGHT;

public class RunConfigEditor extends SettingsEditor<RunConfig> {
    private final MicroconfigRunConfigPanel mcPanel = MicroconfigRunConfigPanel.create();

    @NotNull
    @Override
    protected JComponent createEditor() {
        return mcPanel.getPanel();
    }

    @Override
    protected void resetEditorFrom(@NotNull RunConfig config) {
        this.mcPanel.applyConfig(config);
    }

    @Override
    protected void applyEditorTo(@NotNull RunConfig config) {
        this.mcPanel.updateConfig(config);
    }

    @RequiredArgsConstructor
    static class MicroconfigRunConfigPanel {
        private final JPanel panel;

        private final JTextField envText;
        private final JTextField groupText;
        private final JTextField servicesText;
        private final JTextField destinationText;

        static MicroconfigRunConfigPanel create() {
            JPanel panel = new JPanel();
            GroupLayout layout = new GroupLayout(panel);
            panel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            GroupLayout.ParallelGroup parallel = layout.createParallelGroup();
            layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
            GroupLayout.SequentialGroup sequential = layout.createSequentialGroup();
            layout.setVerticalGroup(sequential);

            List<String> names = asList("Environment: ", "Groups: ", "Services: ", "Destination: ");
            JTextField[] fields = new JTextField[names.size()];
            JLabel[] labels = new JLabel[names.size()];
            for (int i = 0; i < names.size(); i++) {
                labels[i] = new JLabel(names.get(i), RIGHT);
                fields[i] = new JTextField(20);
                labels[i].setLabelFor(fields[i]);

                parallel.addGroup(
                        layout.createSequentialGroup()
                                .addComponent(labels[i])
                                .addComponent(fields[i])
                );
                sequential.addGroup(
                        layout.createParallelGroup(BASELINE)
                                .addComponent(labels[i])
                                .addComponent(fields[i])
                );

                layout.linkSize(HORIZONTAL, labels[i], labels[0]);
            }

            return new MicroconfigRunConfigPanel(panel, fields[0], fields[1], fields[2], fields[3]);
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

        JPanel getPanel() {
            return panel;
        }
    }
}
