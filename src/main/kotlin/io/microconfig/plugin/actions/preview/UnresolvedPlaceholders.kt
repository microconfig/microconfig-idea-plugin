package io.microconfig.plugin.actions.preview

import io.microconfig.core.properties.PlaceholderResolveStrategy
import io.microconfig.plugin.actions.preview.impl.UnresolvedPlaceholderStrategy

object UnresolvedPlaceholders {
    fun strategy(): PlaceholderResolveStrategy = UnresolvedPlaceholderStrategy()
}
