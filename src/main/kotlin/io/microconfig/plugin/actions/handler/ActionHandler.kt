package io.microconfig.plugin.actions.handler

import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext

fun interface ActionHandler {
    fun onAction(context: PluginContext, api: MicroconfigApi)
}
