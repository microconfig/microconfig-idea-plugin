package io.microconfig.plugin.actions.resolve.impl

import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext

class ResolvePlaceholder(private val value: String) : ActionHandler {
    override fun onAction(context: PluginContext, api: MicroconfigApi) {
        val values = api.resolvePlaceholderForEachEnv(value, context.currentFile(), context.projectDir())
        Hints.showHint(value, values, context)
    }
}
