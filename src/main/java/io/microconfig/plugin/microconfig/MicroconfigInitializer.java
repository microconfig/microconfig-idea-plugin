package io.microconfig.plugin.microconfig;


import io.microconfig.factory.ConfigType;
import io.microconfig.factory.MicroconfigFactory;

import java.io.File;

public interface MicroconfigInitializer {
    MicroconfigFactory getMicroconfigFactory(File projectDir);

    ConfigType detectConfigType(File file, File projectDir);

    File findConfigRootDir(File projectDir);
}
