package io.microconfig.plugin.actions.preview;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.MicroconfigAction;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.ConfigOutput;
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

import static com.intellij.openapi.editor.ScrollType.MAKE_VISIBLE;
import static io.microconfig.configs.io.ioservice.selector.FileFormat.YAML;
import static io.microconfig.plugin.microconfig.ConfigOutput.getFileType;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static javax.swing.GroupLayout.Alignment.BASELINE;

public class PreviewAction extends MicroconfigAction {
    private static volatile String lastEnv = "";

    @Override
    protected ActionHandler chooseHandler(PluginContext ignore) {
        return PreviewDialog::create;
    }

    private static class PreviewDialog extends DialogWrapper {
        private final JComponent envPane;
        private final ComboBox<String> envsComboBox = new ComboBox<>(new String[0]);
        private final PreviewText previewText;

        private static void create(PluginContext ctx, MicroconfigApi api) {
            PreviewDialog dialog = new PreviewDialog(ctx, api);
            dialog.init();
            dialog.show();
        }

        private PreviewDialog(PluginContext context, MicroconfigApi api) {
            super(context.getProject());

            this.previewText = new PreviewText(
                    EditorFactory.getInstance().createDocument(""),
                    context.getProject(),
                    getFileType(YAML),
                    true,
                    false);

            Listener listener = new Listener(context, api);
            this.envPane = initEnvPane(context, api, listener);
            listener.updatePreviewText();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            return previewText;
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

        private JComponent initEnvPane(PluginContext context, MicroconfigApi api, ActionListener listener) {
            envsComboBox.setModel(new DefaultComboBoxModel<>(concat(of(""), api.getEnvs(context.projectDir()).stream()).toArray(String[]::new)));
            envsComboBox.setSelectedItem(api.detectEnvOr(context.currentFile(), () -> lastEnv));
            envsComboBox.setEditable(true);
            envsComboBox.addActionListener(listener);

            JLabel envLabel = new JLabel("Environment: ");
            JButton generate = new JButton("Generate");
            generate.addActionListener(listener);

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
                            .addComponent(envsComboBox)
                            .addComponent(generate)
            );

            sequential.addGroup(
                    layout.createParallelGroup(BASELINE)
                            .addComponent(envLabel)
                            .addComponent(envsComboBox)
                            .addComponent(generate));

            return panel;
        }

        @RequiredArgsConstructor
        private class Listener implements ActionListener, KeyListener {
            private final PluginContext context;
            private final MicroconfigApi api;

            @Override
            public void actionPerformed(ActionEvent e) {
                lastEnv = (String) envsComboBox.getSelectedItem();
                updatePreviewText();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == VK_ENTER) {
                    actionPerformed(null);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            private void updatePreviewText() {
                String env = (String) envsComboBox.getSelectedItem();
                setTitle(context.currentFile().getParentFile().getName()
                        + "/" + context.currentFile().getName()
                        + "[" + env + "]"
                        + " result configuration"
                );
                Dimension size = getSize();
                ConfigOutput configOutput = buildConfigs(env);
                previewText.setText(configOutput.getText());
                previewText.setFileType(configOutput.fileType());
                setSize(size.width, size.height);
                moveToTop();
            }

            private void moveToTop() {
                previewText.setCaretPosition(0);

                Editor editor = previewText.getEditor();
                if (editor != null) {
                    editor.getScrollingModel().scrollToCaret(MAKE_VISIBLE);
                }
            }

            private ConfigOutput buildConfigs(String envName) {
                try {
                    return api.buildConfigsForService(context.currentFile(), context.projectDir(), envName);
                } catch (RuntimeException e) {
                    return new ConfigOutput(YAML, e.getMessage());
                }
            }
        }

        private static class PreviewText extends EditorTextField {
            private PreviewText(Document document, Project project, FileType fileType, boolean isViewer, boolean oneLineMode) {
                super(document, project, fileType, isViewer, oneLineMode);
            }

            @Override
            protected EditorEx createEditor() {
                EditorEx editor = super.createEditor();
                editor.setVerticalScrollbarVisible(true);
                editor.setHorizontalScrollbarVisible(true);
                return editor;
            }
        }
    }
}