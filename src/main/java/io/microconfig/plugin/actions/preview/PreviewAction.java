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
import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.actions.handler.MicroconfigAction;
import io.microconfig.plugin.microconfig.ConfigOutput;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.PluginContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static com.intellij.openapi.editor.ScrollType.MAKE_VISIBLE;
import static io.microconfig.configs.io.ioservice.selector.FileFormat.YAML;
import static io.microconfig.plugin.microconfig.ConfigOutput.getFileType;
import static java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static javax.swing.GroupLayout.Alignment.BASELINE;

public class PreviewAction extends MicroconfigAction {
    @Override
    protected ActionHandler chooseHandler(PluginContext ignore) {
        return PreviewDialog::create;
    }

    private static class PreviewDialog extends DialogWrapper {
        private static volatile String lastEnv = "";

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
                    getFileType(YAML));

            Listener listener = new Listener(context, api, this);
            this.envPane = initEnvPane(context, api, listener);
            listener.updatePreviewText();
            getCurrentKeyboardFocusManager().addKeyEventDispatcher(listener);
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
            JButton build = new JButton("Build");
            build.addActionListener(listener);

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
                            .addComponent(build)
            );

            sequential.addGroup(
                    layout.createParallelGroup(BASELINE)
                            .addComponent(envLabel)
                            .addComponent(envsComboBox)
                            .addComponent(build));

            return panel;
        }

        @RequiredArgsConstructor
        private class Listener implements ActionListener, KeyEventDispatcher {
            private final PluginContext context;
            private final MicroconfigApi api;
            private final PreviewDialog dialog;

            @Override
            public void actionPerformed(ActionEvent e) {
                lastEnv = (String) envsComboBox.getSelectedItem();
                updatePreviewText();
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

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dialog.close(0);
                    getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
                }
                return false;
            }
        }

        private static class PreviewText extends EditorTextField {
            private PreviewText(Document document, Project project, FileType fileType) {
                super(document, project, fileType, true, false);
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