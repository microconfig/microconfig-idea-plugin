package io.microconfig.plugin.actions.jump;

import io.microconfig.plugin.actions.common.MicroconfigAction;

public class JumpAction extends MicroconfigAction {
    public JumpAction() {
        super(new JumpHandlerFactory());
    }
}