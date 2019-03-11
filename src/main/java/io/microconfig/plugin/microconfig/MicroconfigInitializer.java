package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;

import java.io.File;

interface MicroconfigInitializer {
    MicroconfigFactory getMicroconfigFactory(File projectDir);

    ConfigType detectConfigType(File file);
}
