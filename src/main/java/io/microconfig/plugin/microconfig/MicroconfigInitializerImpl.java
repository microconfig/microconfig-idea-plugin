package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.plugin.PluginException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

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
