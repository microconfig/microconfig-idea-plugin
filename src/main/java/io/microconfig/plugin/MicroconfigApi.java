package io.microconfig.plugin;

import java.io.File;
import java.util.Map;

public interface MicroconfigApi {
    /**
     *
     * @return Env to Placeholder value
     */
    Map<String, String> resolvePlaceholder(String placeholder);

    File sourceOfPlaceholder(String placeholder);

    File dirForComponent(String componentName);
}
