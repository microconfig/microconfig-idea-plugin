package io.microconfig.plugin.microconfig.impl;

import io.microconfig.core.Microconfig;

import java.io.File;

public interface MicroconfigInitializer {
    Microconfig getMicroconfig(File projectDir);

    File findConfigRootDir(File projectDir);
}