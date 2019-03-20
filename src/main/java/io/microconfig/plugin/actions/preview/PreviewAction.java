package io.microconfig.plugin.actions.preview;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.MicroconfigAction;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_ENTER;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class PreviewAction extends MicroconfigAction {
    @Override
    protected ActionHandler chooseHandler(PluginContext ignore) {
        return PreviewDialog::create;
    }

    private static class PreviewDialog extends DialogWrapper {
        private final JComponent textPane;
        private final JComponent envPane;
        private final JTextField envText = new JTextField("", 20);
        private final JTextPane previewText = new JTextPane();

        private static void create(PluginContext ctx, MicroconfigApi api) {
            PreviewDialog dialog = new PreviewDialog(ctx, api);
            dialog.init();
            dialog.show();
        }

        private PreviewDialog(PluginContext context, MicroconfigApi api) {
            super(context.getProject());

            Listener listener = new Listener(context, api);

            this.textPane = initTextPane();
            this.envPane = initEnvPane(context, api, listener);

            setTitle(context.currentFile().getParentFile().getName() + "/" + context.currentFile().getName() + " result configuration");
            listener.updatePreviewText();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            return textPane;
        }

        @Nullable
        @Override
        protected JComponent createNorthPanel() {
            return envPane;
        }

        @NotNull
        @Override
        protected Action[] createActions() {
            return new Action[]{};
        }

        private JComponent initEnvPane(PluginContext context, MicroconfigApi api, Listener listener) {
            envText.setText(api.detectEnvOr(context.currentFile(), () -> ""));

            JLabel envLabel = new JLabel("Environment: ");

            JButton generate = new JButton("Generate");
            generate.addActionListener(listener);
            envText.addKeyListener(listener);

            JPanel panel = new JPanel();
            GroupLayout layout = new GroupLayout(panel);
            panel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            GroupLayout.ParallelGroup parallel = layout.createParallelGroup();
            layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
            GroupLayout.SequentialGroup sequential = layout.createSequentialGroup();
            layout.setVerticalGroup(sequential);

            parallel.addGroup(
                    layout.createSequentialGroup()
                            .addComponent(envLabel)
                            .addComponent(envText)
                            .addComponent(generate)
            );

            sequential.addGroup(
                    layout.createParallelGroup(BASELINE)
                            .addComponent(envLabel)
                            .addComponent(envText)
                            .addComponent(generate));

            return panel;
        }

        private JComponent initTextPane() {
            previewText.setEditable(false);
            JScrollPane scrollPane = new JBScrollPane(previewText);
            scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
            return scrollPane;
        }

        @RequiredArgsConstructor
        private class Listener implements ActionListener, KeyListener {
            private final PluginContext context;
            private final MicroconfigApi api;

            @Override
            public void actionPerformed(ActionEvent e) {
                updatePreviewText();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == VK_ENTER) {
                    updatePreviewText();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            private void updatePreviewText() {
                Dimension size = getSize();
                previewText.setText(previewTextForEnv(envText.getText()));
                previewText.setCaretPosition(0);
                setSize(size.width, size.height);
            }

            private String previewTextForEnv(String envName) {
                try {
                    return api.buildConfigsForService(context.currentFile(), context.projectDir(), envName);
                } catch (RuntimeException e) {
                    return e.getMessage();
                }
            }
        }
    }
}