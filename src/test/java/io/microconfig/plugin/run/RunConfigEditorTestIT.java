package io.microconfig.plugin.run;

import javax.swing.*;

class RunConfigEditorTestIT {
    public static void main(String[] args) {
        final JPanel panel = RunConfigEditor.MicroconfigRunConfigPanel.create().getPanel();
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

}