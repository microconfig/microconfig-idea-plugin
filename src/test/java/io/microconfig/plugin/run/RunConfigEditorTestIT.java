package io.microconfig.plugin.run;

import javax.swing.*;

class RunConfigEditorTestIT {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(RunConfigEditor.MicroconfigRunConfigPanel.create().getPanel());
        frame.pack();
        frame.setVisible(true);
    }
}