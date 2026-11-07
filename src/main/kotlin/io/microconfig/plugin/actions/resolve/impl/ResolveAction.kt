package io.microconfig.plugin.actions.resolve.impl

import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.actions.handler.MicroconfigAction
import io.microconfig.plugin.actions.resolve.PlaceholderBorder.Companion.borders
import io.microconfig.plugin.microconfig.PluginContext

class ResolveAction : MicroconfigAction() {
    override fun chooseHandler(context: PluginContext): ActionHandler {
        val placeholder = borders(context.currentLine(), context.currentColumn()).value()
        return if (placeholder != null) ResolvePlaceholder(placeholder) else ResolveFullLine()
    }
}
