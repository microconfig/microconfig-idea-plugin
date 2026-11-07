package io.microconfig.plugin.actions.handler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext

abstract class MicroconfigAction : AnAction() {
    final override fun actionPerformed(event: AnActionEvent) {
        val context = PluginContext(event)
        if (context.notFull()) return
        try {
            val handler = chooseHandler(context) ?: return
            handler.onAction(context, MicroconfigApi.create())
        } catch (error: RuntimeException) {
            context.showErrorHint(error)
        }
    }

    protected abstract fun chooseHandler(context: PluginContext): ActionHandler?
}
