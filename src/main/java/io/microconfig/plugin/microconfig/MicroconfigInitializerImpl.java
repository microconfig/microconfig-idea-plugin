package io.microconfig.plugin.microconfig;

import io.microconfig.factory.ConfigType;
import io.microconfig.factory.MicroconfigFactory;
import io.microconfig.factory.StandardConfigTypes;
import io.microconfig.plugin.actions.common.PluginException;

import java.io.File;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.microconfig.configs.io.tree.ComponentTreeCache.COMPONENTS_DIR;
import static io.microconfig.factory.MicroconfigFactory.ENV_DIR;
import static io.microconfig.plugin.utils.FileUtil.findDir;
import static java.util.Arrays.stream;

public class MicroconfigInitializerImpl implements MicroconfigInitializer {
    @Override
    public MicroconfigFactory getMicroconfigFactory(File projectDir) {
        return MicroconfigFactory.init(
                findConfigRootDir(projectDir),
                new File(projectDir, "build/output"),
                new VirtualFileReader()
        );
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
                .filter(ct -> ct.type().getConfigExtensions().stream().anyMatch(e -> e.equals(ext)))
                .map(StandardConfigTypes::type)
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
                .orElseThrow(() -> new PluginException("Can't find 'components' and 'envs' folders on the same level"));
    }
}
