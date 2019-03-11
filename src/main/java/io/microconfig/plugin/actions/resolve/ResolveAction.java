package io.microconfig.plugin.actions.resolve;

import io.microconfig.plugin.actions.common.MicroconfigAction;

public class ResolveAction extends MicroconfigAction {
    public ResolveAction() {
        super(new ResolveHandleFactory());
    }
}
