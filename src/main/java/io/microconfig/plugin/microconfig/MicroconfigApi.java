package io.microconfig.plugin.microconfig;

import io.microconfig.plugin.actions.common.FilePosition;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface MicroconfigApi {
    File findIncludeSource(String includeLine, int currentColumn, File currentFile, File projectDir);

    File findAnyComponentFile(String component, String env, File projectDir);

    FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir);

    /**
     * @return resolved placeholder values for each env: env -> value
     */
    Map<String, String> resolvePlaceholderForEachEnv(String placeholderValue, File currentFile, File projectDir);

    /**
     * @return full line with resolved placeholder values for each env: env -> line
     */
    Map<String, String> resolveFullLineForEachEnv(String currentLine, File currentFile, File projectDir);

    String buildConfigsForService(File currentFile, File projectDir, String env);

    String detectEnvOr(File currentFile, Supplier<String> defaultEnv);

    Set<String> getEnvs(File currentFile);

    MicroconfigInitializer getMicroconfigInitializer();

}
