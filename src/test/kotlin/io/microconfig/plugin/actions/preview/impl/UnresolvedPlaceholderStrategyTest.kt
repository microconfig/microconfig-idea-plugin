package io.microconfig.plugin.actions.preview.impl

import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.plugin.actions.preview.UnresolvedPlaceholders
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UnresolvedPlaceholderStrategyTest {
    private val strategy = UnresolvedPlaceholders.strategy()

    @Test
    fun preservesExternalPlaceholderAsUnresolvedMarker() {
        val property = strategy.resolve("EXTERNAL", "token", "prod", "app", "payments").orElseThrow()

        assertEquals("token", property.key)
        assertEquals("\${EXTERNAL[UNRESOLVED]token}", property.value)
        assertEquals(PROPERTIES, property.configFormat)
        assertEquals("prod", property.declaringComponent!!.environment)
        assertEquals("app", property.declaringComponent!!.configType)
        assertEquals("payments", property.declaringComponent!!.component)
    }

    @Test
    fun leavesOrdinaryAndSystemPlaceholdersToCore() {
        for (component in listOf("eureka", "env", "system", "MixedCase")) {
            assertTrue(strategy.resolve(component, "token", "prod", "app", "payments").isEmpty, component)
        }
    }

    @Test
    fun acceptsAbsentRoot() {
        val property = strategy.resolve("EXTERNAL", "token", "dev", "app", null).orElseThrow()

        assertEquals("", property.declaringComponent!!.component)
    }
}
