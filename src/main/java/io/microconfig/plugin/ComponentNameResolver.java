package io.microconfig.plugin;

import java.util.Optional;

public class ComponentNameResolver {

    public Optional<String> resolve(String currentLine) {
        if (currentLine.startsWith("#include")) return resolveInclude(currentLine);
        return Optional.empty();
    }

    private Optional<String> resolveInclude(String currentLine) {
        return Optional.of(currentLine.substring(8).trim());
    }

}
