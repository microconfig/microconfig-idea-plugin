package io.microconfig.plugin.actions.resolve.impl

import io.microconfig.plugin.microconfig.PluginContext

internal object Hints {
    fun showHint(line: String, valueByEnv: Map<String, String>, context: PluginContext) {
        if (valueByEnv.isEmpty()) {
            context.showErrorHint("Can't resolve '$line'")
            return
        }
        val values = valueByEnv.toMutableMap()
        val hint = StringBuilder().appendLine(line).appendLine()
        addDefaultValue(values, hint)
        addValues(values, hint)
        context.showInfoHint(hint.toString())
    }

    private fun addDefaultValue(values: MutableMap<String, String>, hint: StringBuilder) {
        val default = values.remove("") ?: return
        hint.appendLine("Default: $default").appendLine()
        values.entries.removeIf { it.value == default }
    }

    private fun addValues(values: Map<String, String>, hint: StringBuilder) {
        values.entries.groupBy({ it.value }, { it.key }).toSortedMap().forEach { (value, envs) ->
            hint.appendLine("Value: $value").appendLine("Envs: $envs").appendLine()
        }
    }
}
