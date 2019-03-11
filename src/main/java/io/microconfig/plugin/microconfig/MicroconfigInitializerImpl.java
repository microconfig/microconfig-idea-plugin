package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.commands.buildconfig.factory.StandardConfigType;
import io.microconfig.plugin.actions.common.PluginException;
import io.microconfig.plugin.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.microconfig.commands.buildconfig.factory.MicroconfigFactory.ENV_DIR;
import static io.microconfig.configs.io.tree.ComponentTreeCache.COMPONENTS_DIR;
import static io.microconfig.plugin.utils.FileUtil.find;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.walk;
import static java.util.Arrays.stream;

class MicroconfigInitializerImpl implements MicroconfigInitializer {
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
        return stream(StandardConfigType.values())
                .filter(ct -> ct.getConfigExtensions().stream().anyMatch(e -> e.equals(ext)))
                .map(StandardConfigType::type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find ConfigType for extension " + ext));
    }

    private File findConfigRootDir(File projectDir) {
        Predicate<File> containsMicroconfigDirs = dir -> {
            if (dir.getName().contains(".")) return false;

            File[] files = dir.listFiles();
            return files != null && stream(files)
                    .filter(f -> f.getName().equals(ENV_DIR) || f.getName().equals(COMPONENTS_DIR))
                    .count() == 2;
        };

        return find(projectDir, containsMicroconfigDirs)
                .orElseThrow(() -> new PluginException("Can't find 'components' and 'envs' folders on the same level"));
    }
}
