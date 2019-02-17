package io.microconfig.plugin;

import java.io.File;

public interface MicroconfigApi {
    String resolvePlaceholder(String placeholder);

    File sourceOfPlaceholder(String placeholder);

    File dirForComponent(String componentName);
}
