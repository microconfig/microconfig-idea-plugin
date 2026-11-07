package io.microconfig.plugin.actions.jump.impl

import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext

class JumpFromEnv : ActionHandler {
    override fun onAction(context: PluginContext, api: MicroconfigApi) {
        val component = context.currentToken() // TODO: Support aliases.
        if (component.isEmpty()) return
        val env = context.currentFile().nameWithoutExtension
        val source = api.findAnyComponentFile(component, env, context.projectDir())
        context.navigateTo(source)
    }
}
