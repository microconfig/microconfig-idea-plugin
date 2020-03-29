package io.microconfig.plugin.microconfig.impl;

import io.microconfig.core.Microconfig;
import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.configtypes.ConfigTypeFilters;
import io.microconfig.plugin.microconfig.MicroconfigInitializer;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import static io.microconfig.core.Microconfig.searchConfigsIn;
import static io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithExtensionOf;
import static io.microconfig.core.environments.repository.FileEnvironmentRepository.ENV_DIR;
import static io.microconfig.core.properties.repository.ConfigFileRepositoryImpl.COMPONENTS_DIR;
import static io.microconfig.plugin.utils.FileUtil.findDir;
import static java.util.Arrays.stream;

public class MicroconfigInitializerImpl implements MicroconfigInitializer {
    @Override
    public Microconfig getMicroconfig(File projectDir) {
        File configRoot = findConfigRootDir(projectDir);
        return searchConfigsIn(configRoot)
                .withDestinationDir(new File(projectDir, "build/output"))
                .withFsReader(new VirtualFileReader());
    }

    @Override
    public File findConfigRootDir(File projectDir) {
        Predicate<File> containsMicroconfigDirs = dir -> {
            File[] files = dir.listFiles();
            return files != null && stream(files)
                    .filter(f -> f.getName().equals(ENV_DIR) || f.getName().equals(COMPONENTS_DIR))
                    .count() == 2;
        };

        return findDir(projectDir, containsMicroconfigDirs)
                .orElseThrow(() -> new IllegalStateException("Can't find 'components' and 'envs' folders on the same level"));
    }

    @Override
    public ConfigType detectConfigTypeOf(File currentFile, File projectDir) {
        List<ConfigType> supportedConfigTypes = getMicroconfig(projectDir)
                .getDependencies()
                .getConfigTypeRepository()
                .getConfigTypes();

        return configTypeWithExtensionOf(currentFile)
                .selectTypes(supportedConfigTypes)
                .get(0);
    }
}