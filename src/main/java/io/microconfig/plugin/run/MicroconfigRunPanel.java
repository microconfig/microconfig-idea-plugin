package io.microconfig.plugin.run;

import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

import static javax.swing.GroupLayout.Alignment.LEADING;

public class MicroconfigRunPanel extends JPanel {

    public MicroconfigRunPanel() {
        super();
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel envLabel = new JLabel("Environments: ");
        JTextField envText = new JTextField("textField", 40);

        JLabel groupLabel = new JLabel("Groups: ");
        JTextField groupText = new JTextField("groups", 40);

        JLabel servicesLabel = new JLabel("Services: ");
        JTextField servicesText = new JTextField("service1, service2", 40);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addComponent(envLabel)
                .addComponent(envText)
                .addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(groupLabel)
                    .addComponent(groupText))
                .addGroup(layout.createParallelGroup(LEADING)
                    .addComponent(servicesLabel)
                    .addComponent(servicesText))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(envLabel)
                .addComponent(envText))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(groupLabel)
                .addComponent(groupText))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(servicesLabel)
                .addComponent(servicesText))
        );
    }

    public void applyConfig(MicroconfigRunConfiguration config) {
    }

    public void updateConfig(MicroconfigRunConfiguration config) throws ConfigurationException {

    }

}
