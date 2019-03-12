package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.ConfigType;
import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.configs.Property;
import io.microconfig.configs.provider.Include;
import io.microconfig.configs.resolver.EnvComponent;
import io.microconfig.configs.resolver.PropertyResolver;
import io.microconfig.configs.resolver.PropertyResolverHolder;
import io.microconfig.configs.resolver.placeholder.Placeholder;
import io.microconfig.configs.resolver.placeholder.PlaceholderResolver;
import io.microconfig.configs.sources.FileSource;
import io.microconfig.plugin.actions.common.FilePosition;
import io.microconfig.plugin.actions.common.PluginException;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static io.microconfig.configs.Property.parse;
import static io.microconfig.configs.sources.FileSource.fileSource;
import static io.microconfig.plugin.actions.common.FilePosition.positionFromFileSource;
import static java.lang.Math.max;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class MicroconfigApiImpl implements MicroconfigApi {
    private final MicroconfigInitializer initializer = new MicroconfigInitializerImpl();

    @Override
    public File findIncludeSource(String includeLine, int currentColumn, File currentFile, File projectDir) {
        Supplier<Include> parseInclude = () -> {
            List<Include> includes = Include.parse(includeLine, "");
            if (includes.size() == 1) return includes.get(0);

            int position = currentColumn;
            int componentIndex = -1;
            do {
                ++componentIndex;
                position = includeLine.lastIndexOf(',', max(0, position - 1));
            } while (position > 0);

            return includes.get(componentIndex);
        };

        Include include = parseInclude.get();
        return getSourceFile(projectDir, include.getComponent(), include.getEnv(), currentFile);
    }

    @Override
    public FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir) {
        MicroconfigFactory factory = initializer.getMicroconfigFactory(projectDir);

        Supplier<Placeholder> parsePlaceholder = () -> {
            Placeholder p = Placeholder.parse(placeholderValue, detectEnv(currentFile, factory));
            return p.isSelfReferenced() ? p.changeComponent(currentFile.getParentFile().getName()) : p;
        };

        Placeholder placeholder = parsePlaceholder.get();
        PlaceholderResolver resolver = factory.newPlaceholderResolver(factory.newFileBasedProvider(initializer.detectConfigType(currentFile)));
        Optional<Property> resolved = resolver.resolveToProperty(placeholder);

        if (resolved.isPresent() && resolved.get().getSource() instanceof FileSource) {
            return positionFromFileSource((FileSource) resolved.get().getSource());
        }

        return new FilePosition(getSourceFile(projectDir, placeholder.getComponent(), placeholder.getEnvironment(), currentFile), 0);
    }

    @Override
    public Map<String, String> resolvePlaceholderForEachEnv(String placeholderValue, File currentFile, File projectDir) {
        return resolveFullLineForEachEnv("key=" + placeholderValue, currentFile, projectDir);
    }

    @Override
    public Map<String, String> resolveFullLineForEachEnv(String currentLine, File currentFile, File projectDir) {
        MicroconfigFactory factory = initializer.getMicroconfigFactory(projectDir);
        ConfigType configType = initializer.detectConfigType(currentFile);
        PropertyResolver propertyResolver = ((PropertyResolverHolder) factory.newConfigProvider(configType)).getResolver();

        Property property = parse(currentLine, "", fileSource(currentFile, 0, false));
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

    private File getSourceFile(File projectDir, String component, String env, File currentFile) {
        ConfigType configType = initializer.detectConfigType(currentFile);
        Predicate<File> hasConfigTypeExtension = file -> configType.getConfigExtensions()
                .stream()
                .anyMatch(ext -> file.getName().endsWith(ext));

        Supplier<Comparator<File>> priorityByEnv = () -> {
            Comparator<File> comparator = comparing(f1 -> f1.getName().contains(env + ".") ? 0 : 1);
            return comparator.thenComparing(f -> f.getName().length());
        };


        return initializer.getMicroconfigFactory(projectDir)
                .getComponentTree()
                .getConfigFiles(component, hasConfigTypeExtension)
                .min(priorityByEnv.get())
                .orElseThrow(() -> new PluginException("Component not found: " + component));
    }

    private String detectEnv(File currentFile, MicroconfigFactory factory) {
        String name = currentFile.getName();
        int start = name.indexOf('.');
        int end = name.indexOf('.', start + 1);
        if (end > 0) {
            return name.substring(start + 1, end);
        }

        return factory.getEnvironmentProvider()
                .getEnvironmentNames()
                .stream()
                .findFirst()
                .orElse(""); //otherwise will fail for env-specific props
    }
}