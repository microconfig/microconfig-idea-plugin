package io.microconfig.plugin.run;

import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

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

        JLabel eLabel = new JLabel("Environment:");
        envText = new JTextField(40);

        JLabel gLabel = new JLabel("Groups:");
        groupText = new JTextField(40);

        JLabel sLabel = new JLabel("Services:");
        servicesText = new JTextField(40);

        JLabel fLabel = new JLabel("Destination dir:");
        destinationText = new JTextField(40);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(eLabel)
                                .addComponent(envText))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(gLabel)
                                .addComponent(groupText))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(sLabel)
                                .addComponent(servicesText))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(fLabel)
                                .addComponent(destinationText))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(eLabel)
                        .addComponent(envText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(gLabel)
                        .addComponent(groupText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(sLabel)
                        .addComponent(servicesText))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(fLabel)
                        .addComponent(destinationText))
        );
    }

    void applyConfig(MicroconfigRunConfiguration config) {
        envText.setText(config.getEnv());
        groupText.setText(config.getGroups());
        servicesText.setText(config.getServices());
        destinationText.setText(config.getDestination());
    }

    void updateConfig(MicroconfigRunConfiguration config) throws ConfigurationException {
        config.setEnv(envText.getText());
        config.setGroups(groupText.getText());
        config.setServices(servicesText.getText());
        config.setDestination(destinationText.getText());
    }

}
