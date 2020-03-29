package io.microconfig.plugin.microconfig;

import io.microconfig.core.Microconfig;
import io.microconfig.core.configtypes.ConfigType;

import java.io.File;

public interface MicroconfigInitializer {
    Microconfig getMicroconfig(File projectDir);

    File findConfigRootDir(File projectDir);

    ConfigType detectConfigTypeOf(File currentFile, File projectDir);
}