package io.microconfig.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.microconfig.plugin.utils.ContextUtils.componentType;
import static java.util.Arrays.stream;

public class MicroconfigApiMock implements MicroconfigApi {

    @Override
    public File findInclude(File projectDir, String includeLine, String currentFileName) {
        String componentName = includeComponentName(includeLine);
        String componentType = componentType(currentFileName);

        try (Stream<Path> dirStream = Files.walk(projectDir.toPath())) {
            File[] componentFiles =dirStream
                .map(Path::toFile)
                .filter(File::isDirectory)
                .filter(f -> f.getName().equals(componentName))
                .findFirst()
                .map(File::listFiles)
                .orElseThrow(() -> new PluginException("Component not found: " + componentName));

            return stream(componentFiles)
                .filter(f -> f.getName().endsWith(componentType))
                .findAny()
                .orElseThrow(() -> new PluginException("Component not found: " + componentName));

        } catch (IOException e) {
            throw new PluginException("IOEx: " + e.getMessage());
        }
    }

    private String includeComponentName(String currentLine) {
        return currentLine.substring(8).trim();
    }

}
