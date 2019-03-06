package io.microconfig.plugin.actions.placeholders;

import io.microconfig.plugin.actions.common.MicroconfigAction;

public class ResolvePlaceholderAction extends MicroconfigAction {
    public ResolvePlaceholderAction() {
        super(new PlaceholderHandlerFactory());
    }
}
