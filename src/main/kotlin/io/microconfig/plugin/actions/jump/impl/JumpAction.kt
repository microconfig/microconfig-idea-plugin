package io.microconfig.plugin.actions.jump.impl

import io.microconfig.core.properties.repository.Includes.Companion.isInclude
import io.microconfig.plugin.actions.handler.ActionHandler
import io.microconfig.plugin.actions.handler.MicroconfigAction
import io.microconfig.plugin.actions.resolve.PlaceholderBorder.Companion.borders
import io.microconfig.plugin.microconfig.PluginContext
import java.io.File

class JumpAction : MicroconfigAction() {
    override fun chooseHandler(context: PluginContext): ActionHandler? {
        val line = context.currentLine()
        return when {
            isInclude(line) -> JumpToInclude()
            borders(line, context.currentColumn()).isInsidePlaceholder() -> JumpToPlaceholder()
            isEnvDescriptor(context.currentFile()) -> JumpFromEnv()
            else -> null
        }
    }

    private fun isEnvDescriptor(file: File): Boolean =
        file.extension in listOf("yaml", "json") && "/envs/" in file.parentFile.invariantSeparatorsPath + "/"
}
