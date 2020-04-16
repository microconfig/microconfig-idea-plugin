package io.microconfig.plugin.actions.preview;

import io.microconfig.plugin.actions.handler.ActionHandler;
import io.microconfig.plugin.actions.handler.MicroconfigAction;
import io.microconfig.plugin.microconfig.PluginContext;

public class PreviewAction extends MicroconfigAction {
    @Override
    protected ActionHandler chooseHandler(PluginContext ignore) {
        return PreviewDialog::create;
    }
}