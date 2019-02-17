package io.microconfig.plugin;

import java.util.Optional;

import static io.microconfig.plugin.OptionalUtil.some;
import static java.util.Optional.empty;

public class ComponentNameResolver {
    private static final String INCLUDE = "#include";

    public Optional<String> resolve(String currentLine) {
        return currentLine.startsWith(INCLUDE) ? resolveInclude(currentLine) : empty();
    }

    private Optional<String> resolveInclude(String currentLine) {
        return some(currentLine.substring(INCLUDE.length()).trim());
    }
}
