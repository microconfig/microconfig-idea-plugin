package io.microconfig.plugin.actions.preview;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.MicroconfigAction;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class PreviewAction extends MicroconfigAction {
    @Override
    protected ActionHandler chooseHandler(PluginContext context) {
        return this::onAction;
    }

    private void onAction(PluginContext context, MicroconfigApi api) {
        new PreviewDialog(context, api).show();
    }

    private static class PreviewDialog extends DialogWrapper {
        private final PluginContext context;
        private final MicroconfigApi api;
        private final JComponent textPane;

        PreviewDialog(PluginContext context, MicroconfigApi api) {
            super(context.getProject());

            this.context = context;
            this.api = api;
            this.textPane = textPane();
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            return textPane;
        }

        private JComponent textPane() {
            String preview = api.buildConfigsForService(context.currentFile(), context.projectDir(), "base");

            JTextPane newsTextPane = new JTextPane();
            newsTextPane.setEditable(false);
            newsTextPane.setText(preview);

            JScrollPane scrollPane = new JBScrollPane(newsTextPane);
            scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
            return scrollPane;
        }
    }
}