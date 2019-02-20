package io.microconfig.plugin;

import java.io.File;

public interface MicroconfigApi {

    /**
     * @param projectDir      base directory of microconfig project
     * @param includeLine     #include line
     * @param currentFileName filename of current file for env/type resolution
     * @return file of component if found
     * @throws PluginException if component file not found
     */
    File findInclude(File projectDir, String includeLine, String currentFileName);

}
