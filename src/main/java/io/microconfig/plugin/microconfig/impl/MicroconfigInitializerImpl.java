package io.microconfig.plugin.microconfig.impl;

import io.microconfig.factory.ConfigType;
import io.microconfig.factory.MicroconfigFactory;
import io.microconfig.factory.configtypes.StandardConfigTypes;
import io.microconfig.plugin.microconfig.MicroconfigInitializer;

import java.io.File;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.microconfig.configs.io.components.ComponentTreeCache.COMPONENTS_DIR;
import static io.microconfig.factory.MicroconfigFactory.ENV_DIR;
import static io.microconfig.plugin.utils.FileUtil.findDir;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.of;

public class MicroconfigInitializerImpl implements MicroconfigInitializer {
    @Override
    public MicroconfigFactory getMicroconfigFactory(File projectDir) {
        MicroconfigFactory factory = MicroconfigFactory.init(
                findConfigRootDir(projectDir),
                new File(projectDir, "build/output"),
                new VirtualFileReader()
        );
        of(StandardConfigTypes.values()).forEach(t -> factory.newConfigProvider(t.getType()));
        return factory;
    }

    @Override
    public ConfigType detectConfigType(File file) {
        Supplier<String> fileExtension = () -> {
            String name = file.getName();
            int lastDot = name.lastIndexOf('.');
            if (lastDot >= 0) return name.substring(lastDot);

            throw new IllegalArgumentException("File " + file + " doesn't have an extension. Unable to resolve component type.");
        };

        String ext = fileExtension.get();
        return stream(StandardConfigTypes.values())
                .filter(ct -> ct.getType().getSourceExtensions().stream().anyMatch(e -> e.equals(ext)))
                .map(StandardConfigTypes::getType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find ConfigType for extension " + ext));
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
}
