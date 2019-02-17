package io.microconfig.plugin;

import java.io.File;
import java.util.Map;

public interface MicroconfigApi {
    /**
     *
     * @return Env to Placeholder value
     */
    Map<String, String> placeholderValues(File projectDir, String placeholder);

    File placeholderSourceLocation(File projectDir, String placeholder);

    File componentDirectory(File projectDir, String componentName);

    File componentFile(File projectDir, String componentName, String componentType);
}
