package io.microconfig.plugin.microconfig;

import io.microconfig.commands.Command;
import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;

import java.io.File;

interface MicroconfigInitializer {
    MicroconfigFactory getMicroconfigFactory(File projectDir);

    Command newBuildCommand(File projectRoot, File destination);

    ConfigType detectConfigType(File file);
}
