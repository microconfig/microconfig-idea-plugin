package io.microconfig.plugin.microconfig;

import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.PluginException;

import java.io.File;
import java.util.Map;

public interface MicroconfigApi {
    String INCLUDE = "#include";

    /**
     * @param projectDir      base directory of microconfig project
     * @param includeLine     #include line
     * @param currentFileName filename of current file for env/type resolution
     * @return {@link java.io.File} of component if found
     * @throws PluginException if component file not found
     */
    File findInclude(File projectDir, String includeLine, String currentFileName);

    /**
     * @param projectDir  base directory of microconfig project
     * @param placeholder placeholder string value
     * @param currentFile current file for env/type resolution
     * @return {@link FilePosition}  with file of component if found and line number inside file
     * @throws PluginException if component/key not found
     */
    FilePosition findPlaceholderKey(File projectDir, String placeholder, File currentFile);

    /**
     * @param projectDir  base directory of microconfig project
     * @param currentLine current line to resolve placeholders
     * @return full line with resolved placeholder values for each env: env -> line
     */
    Map<String, String> lineWithPlaceholders(File projectDir, String currentLine);

    /**
     * @param projectDir  base directory of microconfig project
     * @param placeholder placeholder
     * @return resolved placeholder values for each env: env -> value
     */
    Map<String, String> placeholderValues(File projectDir, String placeholder);

    /**
     * @param placeholder string value of placeholder
     * @return true if placeholder is navigatable, false otherwise
     */
    boolean navigatable(String placeholder);

}
