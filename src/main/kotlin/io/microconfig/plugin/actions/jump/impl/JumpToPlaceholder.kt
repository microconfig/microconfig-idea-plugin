package io.microconfig.plugin.actions.jump.impl

import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.actions.resolve.PlaceholderBorder.Companion.borders
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext

class JumpToPlaceholder : ActionHandler {
    override fun onAction(context: PluginContext, api: MicroconfigApi) {
        val placeholder = borders(context.currentLine(), context.currentColumn()).value() ?: return
        api.findPlaceholderSource(placeholder, context.currentFile(), context.projectDir()).moveToPosition(context)
    }
}
