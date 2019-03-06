package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.commands.buildconfig.factory.StandardConfigType;
import io.microconfig.plugin.actions.common.PluginException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.microconfig.plugin.utils.FileUtil.fileExtension;
import static java.nio.file.Files.walk;
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
        String ext = fileExtension(file);
        return stream(StandardConfigType.values())
                .filter(ct -> ct.getConfigExtensions().stream().anyMatch(e -> e.equals(ext)))
                .map(StandardConfigType::type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find ConfigType for extension " + ext));
    }

    private File findConfigRootDir(File projectDir) {
        if (projectDir.isDirectory() && containsMicroconfigDirs(projectDir.listFiles())) return projectDir;

        try (Stream<Path> walk = walk(projectDir.toPath())) {
            return walk.map(Path::toFile)
                    .filter(File::isDirectory)
                    .filter(f -> containsMicroconfigDirs(f.listFiles()))
                    .findAny()
                    .orElseThrow(() -> new PluginException("Can't find 'components' and 'envs' folders on same level"));
        } catch (IOException e) {
            throw new PluginException("IO exception " + e.getMessage());
        }
    }

    private boolean containsMicroconfigDirs(File[] files) {
        return stream(files)
                .filter(File::isDirectory)
                .filter(f -> f.getName().equals("components") || f.getName().equals("envs"))
                .count() == 2;
    }
}
