package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.commands.buildconfig.factory.StandardConfigType;
import io.microconfig.configs.Property;
import io.microconfig.configs.PropertySource;
import io.microconfig.configs.provider.Include;
import io.microconfig.configs.resolver.placeholder.Placeholder;
import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.PluginException;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.microconfig.environments.Component.byType;
import static io.microconfig.plugin.utils.ContextUtils.fileExtension;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;

public class MicroconfigApiImpl implements MicroconfigApi {
    private final MicroconfigInitializer initializer = new MicroconfigInitializerImpl();

    @Override
    public File findIncludeSource(String includeLine, File currentFile, File projectDir) {
        Include include = Include.parse(includeLine, "").get(0); //todo

        Supplier<Comparator<File>> priorityByEnv = () -> {
            Comparator<File> comparator = comparing(f1 -> f1.getName().contains(include.getEnv() + ".") ? 0 : 1);
            return comparator.thenComparing(f -> f.getName().length());
        };

        String componentName = include.getComponent();
        String fileExtension = fileExtension(currentFile.getName());
        return initializer.getMicroconfigFactory(projectDir)
                .getComponentTree()
                .getConfigFiles(componentName, file -> file.getName().endsWith(fileExtension))
                .min(priorityByEnv.get())
                .orElseThrow(() -> new PluginException("Component not found: " + componentName));
    }

    @Override
    public FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir) {
        MicroconfigFactory factory = initializer.getMicroconfigFactory(projectDir);
        Placeholder p = getPlaceholder(placeholderValue, currentFile, factory);

        Map<String, Property> properties = factory
                .newConfigProvider(configTypeBy(fileExtension(currentFile.getName())))
                .getProperties(byType(p.getComponent()), p.getEnvironment());
        Property property = properties.get(p.getValue());

        if (property == null) {
            throw new PluginException("Can't resolve " + placeholderValue);
        }

        PropertySource source = property.getSource();
        return new FilePosition(new File(source.getSourceOfProperty()), source.getLine());
    }

    private Placeholder getPlaceholder(String placeholder, File currentFile, MicroconfigFactory factory) {
        Placeholder p = Placeholder.parse(placeholder, anyEnv(factory));
        return p.getComponent().equals("this") ? p.changeComponent(currentFile.getParentFile().getName()) : p;
    }

    private ConfigType configTypeBy(String ext) {
        return Stream.of(StandardConfigType.values())
                .filter(ct -> ct.getConfigExtensions().stream().anyMatch(e -> e.equals(ext)))
                .map(StandardConfigType::type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find ConfigType for extension " + ext));
    }

    private String anyEnv(MicroconfigFactory factory) {
        return factory.getEnvironmentProvider()
                .getEnvironmentNames()
                .stream()
                .findFirst()
                .orElse(""); //otherwise will fail for env-specific props
    }

    @Override
    public Map<String, String> resolvePropertyValueForEachEnv(String currentLine, File projectDir) {
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
    public Map<String, String> resolveOnePlaceholderForEachEnv(String placeholder, File projectDir) {
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
}