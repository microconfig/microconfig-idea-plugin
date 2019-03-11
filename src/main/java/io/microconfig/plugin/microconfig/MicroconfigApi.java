package io.microconfig.plugin.microconfig;

import io.microconfig.plugin.actions.common.FilePosition;

import java.io.File;
import java.util.Map;

public interface MicroconfigApi {
    File findIncludeSource(String includeLine, int currentColumn, File currentFile, File projectDir);

    FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir);

    /**
     * @return resolved placeholder values for each env: env -> value
     */
    Map<String, String> resolvePlaceholderForEachEnv(String placeholderValue, File currentFile, File projectDir);

    /**
     * @return full line with resolved placeholder values for each env: env -> line
     */
    Map<String, String> resolveFullLineForEachEnv(String currentLine, File currentFile, File projectDir);

    /**
     * @return true if placeholder is navigatable, false otherwise
     */
    boolean navigatable(String placeholder);
}
