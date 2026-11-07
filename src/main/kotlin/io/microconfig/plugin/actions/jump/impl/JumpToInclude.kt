package io.microconfig.plugin.actions.jump.impl

import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext

class JumpToInclude : ActionHandler {
    override fun onAction(context: PluginContext, api: MicroconfigApi) {
        val source = api.findIncludeSource(context.currentLine(), context.currentColumn(), context.currentFile(), context.projectDir())
        context.navigateTo(source)
    }
}
