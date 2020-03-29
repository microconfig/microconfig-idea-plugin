package io.microconfig.plugin.microconfig;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface MicroconfigApi {
    File findIncludeSource(String includeLine, int currentColumn, File currentFile, File projectDir);

    File findAnyComponentFile(String component, String env, File projectDir);

    FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir);

    Map<String, String> resolvePlaceholderForEachEnv(String placeholderValue, File currentFile, File projectDir);

    Map<String, String> resolveFullLineForEachEnv(String currentLine, File currentFile, File projectDir);

    String detectEnvOr(File currentFile, Supplier<String> defaultEnv);

    Set<String> getEnvs(File projectDir);

    ConfigOutput buildConfigs(File currentFile, File projectDir, String env);
}