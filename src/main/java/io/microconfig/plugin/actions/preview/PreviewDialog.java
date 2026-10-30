package io.microconfig.plugin.actions.preview;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import io.microconfig.plugin.microconfig.ConfigOutput;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.PluginContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.intellij.openapi.actionSystem.CommonShortcuts.ESCAPE;
import static com.intellij.openapi.editor.ScrollType.MAKE_VISIBLE;
import static io.microconfig.core.properties.ConfigFormat.YAML;
import static io.microconfig.plugin.microconfig.ConfigOutput.getFileType;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static javax.swing.GroupLayout.Alignment.BASELINE;

class PreviewDialog extends DialogWrapper {
    private static volatile String lastEnv = "";

    private final JComponent envPane;
    private final ComboBox<String> envsComboBox = new ComboBox<>(new String[0]);
    private final PreviewText previewText;

    public static void create(PluginContext ctx, MicroconfigApi api) {
        var dialog = new PreviewDialog(ctx, api);
        dialog.init();
        dialog.show();
    }

    PreviewDialog(PluginContext context, MicroconfigApi api) {
        super(context.getProject());

        this.previewText = new PreviewText(
                EditorFactory.getInstance().createDocument(""),
                context.getProject(),
                getFileType(YAML));

        BuildConfigsListener listener = new BuildConfigsListener(context, api);
        this.envPane = initEnvPane(context, api, listener);
        listener.updatePreviewText();
        closeOnEscapeIn(previewText);
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


    @Override
    protected Action[] createActions() {
        return new Action[]{};
    }

    private JComponent initEnvPane(PluginContext context, MicroconfigApi api, ActionListener listener) {
        envsComboBox.setModel(new DefaultComboBoxModel<>(concat(of(""), api.getEnvs(context.projectDir()).stream()).toArray(String[]::new)));
        envsComboBox.setSelectedItem(api.detectEnvOr(context.currentFile()).orElseGet(() -> lastEnv));
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

    private void closeOnEscapeIn(JComponent component) {
        DumbAwareAction.create(e -> doCancelAction())
                .registerCustomShortcutSet(ESCAPE, component, getDisposable());
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

    @RequiredArgsConstructor
    private class BuildConfigsListener implements ActionListener {
        private final PluginContext context;
        private final MicroconfigApi api;

        @Override
        public void actionPerformed(ActionEvent e) {
            lastEnv = (String) envsComboBox.getSelectedItem();
            Dimension size = getSize();
            updatePreviewText();
            setSize(size.width, size.height);
        }

        private void updatePreviewText() {
            String env = (String) envsComboBox.getSelectedItem();
            setTitle(context.currentFile().getParentFile().getName()
                    + "/" + context.currentFile().getName()
                    + "[" + env + "]"
                    + " result configuration"
            );
            ConfigOutput configOutput = buildConfigs(env);
            previewText.setText(configOutput.getText());
            previewText.setFileType(configOutput.fileType());
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
                return api.buildConfigs(context.currentFile(), context.projectDir(), envName);
            } catch (RuntimeException e) {
                return new ConfigOutput(YAML, e.getMessage());
            }
        }
    }
}
