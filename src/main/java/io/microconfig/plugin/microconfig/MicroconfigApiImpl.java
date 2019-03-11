package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.configs.Property;
import io.microconfig.configs.provider.Include;
import io.microconfig.configs.resolver.EnvComponent;
import io.microconfig.configs.resolver.PropertyResolver;
import io.microconfig.configs.resolver.PropertyResolverHolder;
import io.microconfig.configs.resolver.placeholder.Placeholder;
import io.microconfig.plugin.actions.common.FilePosition;
import io.microconfig.plugin.actions.common.PluginException;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static io.microconfig.configs.Property.parse;
import static io.microconfig.configs.PropertySource.fileSource;
import static io.microconfig.environments.Component.byType;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class MicroconfigApiImpl implements MicroconfigApi {
    private final MicroconfigInitializer initializer = new MicroconfigInitializerImpl();

    @Override
    public File findIncludeSource(String includeLine, int currentColumn, File currentFile, File projectDir) {
        Supplier<Include> parseInclude = () -> {
            List<Include> includes = Include.parse(includeLine, "");
            if (includeLine.length() == 1) return includes.get(0);

            int order = 0;
            int p = currentColumn - 1;
            while (p > 0) {
                p = includeLine.lastIndexOf(',', p) - 1;
                ++order;
            }
            return includes.get(order);
        };

        Include include = parseInclude.get();

        ConfigType configType = initializer.detectConfigType(currentFile);
        Predicate<File> hasConfigTypeExtension = file -> configType.getConfigExtensions()
                .stream()
                .anyMatch(ext -> file.getName().endsWith(ext));

        Supplier<Comparator<File>> priorityByEnv = () -> {
            Comparator<File> comparator = comparing(f1 -> f1.getName().contains(include.getEnv() + ".") ? 0 : 1);
            return comparator.thenComparing(f -> f.getName().length());
        };

        return initializer.getMicroconfigFactory(projectDir)
                .getComponentTree()
                .getConfigFiles(include.getComponent(), hasConfigTypeExtension)
                .min(priorityByEnv.get())
                .orElseThrow(() -> new PluginException("Component not found: " + include.getComponent()));
    }

    @Override
    public FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir) {
        MicroconfigFactory factory = initializer.getMicroconfigFactory(projectDir);

        Supplier<Placeholder> parsePlaceholder = () -> {
            Placeholder p = Placeholder.parse(placeholderValue, anyEnv(factory));
            return p.isSelfReferenced() ? p.changeComponent(currentFile.getParentFile().getName()) : p;
        };

        Placeholder p = parsePlaceholder.get();
        Property property = factory
                .newConfigProvider(initializer.detectConfigType(currentFile))
                .getProperties(byType(p.getComponent()), p.getEnvironment())
                .get(p.getValue());
        if (property == null) {
            throw new PluginException("Can't resolve " + placeholderValue); //todo return FilePosition(componentFile, 0)
        }

        return new FilePosition(property.getSource());
    }

    @Override
    public Map<String, String> resolveOnePlaceholderForEachEnv(String placeholderValue, File currentFile, File projectDir) {
        return resolvePropertyValueForEachEnv("key=" + placeholderValue, currentFile, projectDir);
    }

    @Override
    public Map<String, String> resolvePropertyValueForEachEnv(String currentLine, File currentFile, File projectDir) {
        MicroconfigFactory factory = initializer.getMicroconfigFactory(projectDir);
        PropertyResolver propertyResolver = ((PropertyResolverHolder) factory
                .newConfigProvider(initializer.detectConfigType(currentFile)))
                .getResolver();

        Property property = parse(currentLine, "", fileSource(currentFile, -1, false));
        UnaryOperator<String> resolve = env -> {
            try {
                Property p = property.withNewEnv(env);
                return propertyResolver.resolve(p, new EnvComponent(p.getSource().getComponent(), p.getEnvContext()));
            } catch (RuntimeException e) {
                return "ERROR";
            }
        };

        return factory.getEnvironmentProvider()
                .getEnvironmentNames()
                .stream()
                .collect(toMap(identity(), resolve));
    }

    @Override
    public boolean navigatable(String placeholder) {
        return true;
    }

    private String anyEnv(MicroconfigFactory factory) {
        return factory.getEnvironmentProvider()
                .getEnvironmentNames()
                .stream()
                .findFirst()
                .orElse(""); //otherwise will fail for env-specific props
    }
}