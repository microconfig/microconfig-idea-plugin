package io.microconfig.plugin.actions.preview;

import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.MicroconfigAction;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;

import javax.swing.*;

public class PreviewAction extends MicroconfigAction {

    @Override
    protected ActionHandler chooseHandler(PluginContext context) {
        return this::onAction;
    }

    void onAction(PluginContext context, MicroconfigApi api) {
        String preview = api.buildConfigsForService(context.currentFile(), context.projectDir(), "base");

        JTextPane newsTextPane = new JTextPane();
        newsTextPane.setEditable(false);
        newsTextPane.setText(preview);

        JScrollPane scrollPane = new JScrollPane(newsTextPane);
        scrollPane.setVerticalScrollBarPolicy(
            javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        ComponentPopupBuilder textWindow = JBPopupFactory.getInstance().createComponentPopupBuilder(scrollPane, null);
        JBPopup popup = textWindow.createPopup();
        popup.showInBestPositionFor(context.getEditor());
    }

}