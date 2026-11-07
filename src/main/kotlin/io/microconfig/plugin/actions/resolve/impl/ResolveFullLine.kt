package io.microconfig.plugin.actions.resolve.impl

import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.PropertyImpl.Companion.parse
import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext

class ResolveFullLine : ActionHandler {
    override fun onAction(context: PluginContext, api: MicroconfigApi) {
        val property = parse(context.currentLine(), PROPERTIES, DeclaringComponentImpl("", "", ""))
        val values = api.resolveFullLineForEachEnv(property.value, context.currentFile(), context.projectDir())
        Hints.showHint(property.key, values, context)
    }
}
