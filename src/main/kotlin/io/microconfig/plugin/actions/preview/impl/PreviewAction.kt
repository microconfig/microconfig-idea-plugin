package io.microconfig.plugin.actions.preview.impl

import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.actions.handler.MicroconfigAction
import io.microconfig.plugin.microconfig.PluginContext

class PreviewAction : MicroconfigAction() {
    override fun chooseHandler(context: PluginContext): ActionHandler = ActionHandler(PreviewDialog::create)
}
