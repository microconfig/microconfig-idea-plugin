package io.microconfig.plugin.microconfig;

import io.microconfig.commands.factory.ConfigType;
import io.microconfig.commands.factory.MicroconfigFactory;
import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.PluginException;
import io.microconfig.properties.Property;
import io.microconfig.properties.Property.Source;
import io.microconfig.properties.files.parser.Include;
import io.microconfig.properties.resolver.placeholder.Placeholder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static io.microconfig.environments.Component.byType;
import static io.microconfig.plugin.utils.ContextUtils.fileExtension;
import static java.nio.file.Files.walk;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;

public class MicroconfigApiImpl implements MicroconfigApi {
    @Override
    public File findInclude(File projectDir, String includeLine, String currentFileName) {
        Include include = Include.parse(includeLine, "");

        String componentName = include.getComponentName();
        String fileExtension = fileExtension(currentFileName);

        return microconfigFactory(projectDir)
                .getComponentTree()
                .getConfigFiles(componentName, file -> file.getName().endsWith(fileExtension))
                .min(priorityByEnv(include.getEnv()))
                .orElseThrow(() -> new PluginException("Component not found: " + componentName));
    }

    private Comparator<File> priorityByEnv(String env) {
        Comparator<File> comparator = comparing(f1 -> f1.getName().contains(env + ".") ? 0 : 1);
        return comparator.thenComparing(f -> f.getName().length());
    }

    @Override
    public FilePosition findPlaceholderKey(File projectDir, String placeholder, File currentFile) {
        MicroconfigFactory factory = microconfigFactory(projectDir);
        Placeholder p = getPlaceholder(placeholder, currentFile, factory);

        Map<String, Property> properties = factory
                .newPropertiesProvider(byExtension(fileExtension(currentFile.getName()))) //todo useVirtualFiles
                .getProperties(byType(p.getComponent()), p.getEnvironment());
        Property property = properties.get(p.getValue());

        if (property == null) {
            throw new PluginException("Can't resolve " + placeholder);
        }

        Source source = property.getSource();
        return new FilePosition(new File(projectDir, source.getSourceOfProperty()), source.getLine());
    }

    private ConfigType byExtension(String ext) {
        return Stream.of(ConfigType.values())
                .filter(ct -> ct.getConfigExtension().equals(ext))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find ConfigType for extension " + ext));
    }

    private Placeholder getPlaceholder(String placeholder, File currentFile, MicroconfigFactory factory) {
        Placeholder p = Placeholder.parse(placeholder, anyEnv(factory));
        return p.getComponent().equals("this") ? p.changeComponent(currentFile.getParentFile().getName()) : p;
    }

    private String anyEnv(MicroconfigFactory factory) {
        return factory.getEnvironmentProvider()
                .getEnvironmentNames()
                .stream()
                .findFirst()
                .orElse(""); //otherwise will fail for env-specific props
    }

    @Override
    public Map<String, String> lineWithPlaceholders(File projectDir, String currentLine) {
        return new Random().nextBoolean() ? emptyMap()
                : new HashMap<String, String>() {
            {
                put("prod", "key=prod value");
                put("dev", "key=dev value");
                put("", "key=default value");
            }
        };
    }

    @Override
    public Map<String, String> placeholderValues(File projectDir, String placeholder) {
        return new Random().nextBoolean() ? emptyMap()
                : new HashMap<String, String>() {
            {
                put("prod", "prod value");
                put("dev", "dev value");
                put("", "default value");
            }
        };
    }

    @Override
    public boolean navigatable(String placeholder) {
        return true;
    }

    private MicroconfigFactory microconfigFactory(File projectDir) {
        File configRootDir = findConfigRootDir(projectDir);
        System.out.println("Config root dir: " + configRootDir);
        return MicroconfigFactory.init(
                configRootDir,
                new File(projectDir, "build/output")
        );
    }

    private static File findConfigRootDir(File projectDir) {
        if (projectDir.isDirectory() && configRootDir(projectDir.listFiles())) return projectDir;

        try (Stream<Path> walk = walk(projectDir.toPath())) {
            return walk.map(Path::toFile)
                    .filter(File::isDirectory)
                    .filter(f -> configRootDir(f.listFiles()))
                    .findAny()
                    .orElseThrow(() -> new PluginException("Can't find 'components' and 'envs' folders on same level"));
        } catch (IOException e) {
            throw new PluginException("IO exception " + e.getMessage());
        }
    }

    private static boolean configRootDir(File[] children) {
        return stream(children)
                .filter(File::isDirectory)
                .filter(f -> f.getName().equals("components") || f.getName().equals("envs"))
                .count() == 2;
    }
}