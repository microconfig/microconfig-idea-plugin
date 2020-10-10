package io.microconfig.plugin.actions.preview;

import io.microconfig.core.properties.DeclaringComponentImpl;
import io.microconfig.core.properties.PlaceholderResolveStrategy;
import io.microconfig.core.properties.Property;

import java.util.Optional;

import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.PropertyImpl.property;
import static java.util.Optional.empty;

public class UnresolvedPlaceholderStrategy implements PlaceholderResolveStrategy {
    @Override
    public Optional<Property> resolve(String component, String key, String environment, String configType, String root) {
        if (!component.toUpperCase().equals(component)) return empty();
        var unresolved = String.format("${%s[UNRESOLVED]%s}", component, key);
        var p = property(key, unresolved, PROPERTIES, new DeclaringComponentImpl(configType, root, environment));
        return Optional.of(p);
    }
}
