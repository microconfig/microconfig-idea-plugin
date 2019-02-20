package io.microconfig.plugin;

import java.io.File;

public interface MicroconfigApi {

    /**
     * @param projectDir      base directory of microconfig project
     * @param includeLine     #include line
     * @param currentFileName filename of current file for env/type resolution
     * @return {@link java.io.File} of component if found
     * @throws PluginException if component file not found
     */
    File findInclude(File projectDir, String includeLine, String currentFileName);

    /**
     * @param projectDir      base directory of microconfig project
     * @param placeholder     placeholder string value
     * @param currentFileName filename of current file for env/type resolution
     * @return {@link FilePosition}  with file of component if found and line number inside file
     * @throws PluginException if component/key not found
     */
    FilePosition findPlaceholderKey(File projectDir, String placeholder, String currentFileName);

    /**
     * @param placeholder string value of placeholder
     * @return true if placeholder is navigatable, false otherwise
     */
    boolean navigatable(String placeholder);

    /**
     * @param line current line
     * @param offset offset inside current line
     * @return true if inside ${placeholder} false otherwise
     */
    boolean insidePlaceholder(String line, int offset);
}
