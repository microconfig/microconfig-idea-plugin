package io.microconfig.plugin.actions.preview.impl

import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.DeclaringComponentImpl
import io.microconfig.core.properties.PlaceholderResolveStrategy
import io.microconfig.core.properties.Property
import io.microconfig.core.properties.PropertyImpl.Companion.property
import java.util.Locale
import java.util.Optional

class UnresolvedPlaceholderStrategy : PlaceholderResolveStrategy {
    override fun resolve(component: String, key: String, environment: String, configType: String, root: String?): Optional<Property> {
        if (component.uppercase(Locale.ROOT) != component) return Optional.empty()
        val unresolved = "\${$component[UNRESOLVED]$key}"
        return Optional.of(property(key, unresolved, PROPERTIES, DeclaringComponentImpl(configType, root.orEmpty(), environment)))
    }
}
