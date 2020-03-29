package io.microconfig.plugin.microconfig.impl;

import io.microconfig.core.Microconfig;
import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.properties.*;
import io.microconfig.core.properties.Properties;
import io.microconfig.core.properties.repository.Include;
import io.microconfig.core.properties.repository.Includes;
import io.microconfig.core.properties.resolvers.placeholder.Placeholder;
import io.microconfig.plugin.microconfig.ConfigOutput;
import io.microconfig.plugin.microconfig.FilePosition;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigInitializer;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithExtensionOf;
import static io.microconfig.core.configtypes.StandardConfigType.APPLICATION;
import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.ConfigFormat.YAML;
import static io.microconfig.core.properties.resolvers.placeholder.PlaceholderBorders.findPlaceholderIn;
import static io.microconfig.core.properties.serializers.PropertySerializers.asString;
import static java.lang.Math.max;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class MicroconfigApiImpl implements MicroconfigApi {
    private final MicroconfigInitializer initializer = new MicroconfigInitializerImpl();

    @Override
    public File findIncludeSource(String includeLine, int currentColumn, File currentFile, File projectDir) {
        Supplier<Include> parseInclude = () -> {
            List<Include> includes = Includes.from(includeLine).withDefaultEnv("");
            if (includes.size() == 1) return includes.get(0);

            int componentIndex = getIncludedComponentIndex(includeLine, currentColumn);
            return includes.get(componentIndex);
        };

        Include include = parseInclude.get();
        return findSourceFile(include.getComponent(), include.getEnvironment(), currentFile, projectDir);
    }

    @Override
    public FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir) {
        Placeholder placeholder = findPlaceholderIn(placeholderValue)
                .orElseThrow(() -> new IllegalStateException("Can't parse " + placeholderValue))
                .toPlaceholder("some", "some");

        Optional<Property> resolved = initializer.getMicroconfig(projectDir)
                .environments()
                .getOrCreateByName(detectEnvOr(currentFile, () -> ""))
                .getOrCreateComponentWithName(placeholder.getComponent())
                .getPropertiesFor(configTypeWithExtensionOf(currentFile))
                .getPropertyWithKey(placeholder.getKey());

        return resolved.map(Property::getDeclaringComponent)
                .filter(dc -> dc instanceof FileBasedComponent)
                .map(FileBasedComponent.class::cast)
                .map(FilePosition::positionFromFileSource)
                .orElseGet(() -> {
                    File sourceFile = findSourceFile(placeholder.getComponent(), placeholder.getEnvironment(), currentFile, projectDir);
                    return new FilePosition(sourceFile, 0);
                });
    }

    @Override
    public Map<String, String> resolvePlaceholderForEachEnv(String placeholderValue, File currentFile, File projectDir) {
        return resolveFullLineForEachEnv("key=" + placeholderValue, currentFile, projectDir);
    }

    @Override
    public Map<String, String> resolveFullLineForEachEnv(String currentLine, File currentFile, File projectDir) {
        Microconfig microconfig = initializer.getMicroconfig(projectDir);
        UnaryOperator<String> resolveValue = env -> {
            try {

                Property p = property.withNewEnv(env);
                return propertyResolver.resolve(p, new EnvComponent(p.getSource().getComponent(), p.getEnvContext()));
            } catch (RuntimeException e) {
                return "ERROR";
            }
        };

        return envs(currentLine, currentFile, microconfig)
                .collect(toMap(identity(), resolveValue, (k1, k2) -> k1, TreeMap::new));
    }

    @Override
    public File findAnyComponentFile(String component, String env, File projectDir) {
        try {
            return findFile(component, env, containsConfigTypeExtension(APPLICATION), projectDir);
        } catch (RuntimeException e) {
            return findFile(component, env, f -> true, projectDir);
        }
    }

    @Override
    public ConfigOutput buildConfigs(File currentFile, File projectDir, String env) {
        Microconfig microconfig = initializer.getMicroconfig(projectDir);
        TypedProperties properties = microconfig.inEnvironment(env)
                .getOrCreateComponentWithName(currentFile.getParentFile().getName()) //todo alias
                .getPropertiesFor(configTypeWithExtensionOf(currentFile))
                .first()
                .resolveBy(microconfig.resolver());

        return new ConfigOutput(
                properties.getProperties().stream().anyMatch(p->p.getConfigFormat() == YAML) ? YAML : PROPERTIES,
                properties.save(asString())
        );
    }

    private Stream<String> envs(String currentLine, File currentFile, Microconfig microconfig) {
        if (!Placeholder.parse(new StringBuilder(currentLine)).isValid()) return of("");

        if (currentFile.getName().indexOf('.') != currentFile.getName().lastIndexOf('.')) {
            String[] parts = currentFile
                    .getName()
                    .split("\\.");

            return stream(parts)
                    .skip(1)
                    .limit(parts.length - 2);
        }

        return concat(
                of(""),
                microconfig.environments().environmentNames().stream()
        );
    }

    private Predicate<File> containsConfigTypeExtension(ConfigType configType) {
        return file -> configType.getSourceExtensions()
                .stream()
                .anyMatch(ext -> file.getName().endsWith(ext));
    }

    private File findFile(String component, String env, Predicate<File> predicate, File projectDir) {
        Supplier<Comparator<File>> priorityByEnv = () -> {
            Comparator<File> comparator = comparing(f1 -> f1.getName().contains(env + ".") ? 0 : 1);
            return comparator.thenComparing(f -> f.getName().length());
        };

        return initializer.getMicroconfig(projectDir)
                .getComponentTree()
                .getConfigFiles(component, predicate)
                .min(priorityByEnv.get())
                .orElseThrow(() -> new IllegalStateException("Component not found: " + component));
    }

    @Override
    public String detectEnvOr(File currentFile, Supplier<String> defaultEnv) {
        String name = currentFile.getName();
        int start = name.indexOf('.');
        int end = name.indexOf('.', start + 1);
        if (end > 0) {
            return name.substring(start + 1, end);
        }

        return defaultEnv.get();
    }

    private Supplier<String> anyEnv(Microconfig microconfig) {
        return () -> {
            return microconfig.environments()
                    .environmentNames()
                    .stream()
                    .findFirst()
                    .orElse(""); //otherwise will fail for env-specific prop
        };
    }

    @Override
    public Set<String> getEnvs(File projectDir) {
        return initializer.getMicroconfig(projectDir)
                .environments()
                .environmentNames();
    }

    private int getIncludedComponentIndex(String includeLine, int currentColumn) {
        int position = currentColumn;
        int componentIndex = -1;
        do {
            ++componentIndex;
            position = includeLine.lastIndexOf(',', max(0, position - 1));
        } while (position > 0);
        return componentIndex;
    }

    private File findSourceFile(String component, String env, File currentFile, File projectDir) {
        return findFile(component, env, containsConfigTypeExtension(initializer.detectConfigType(currentFile, projectDir)), projectDir);
    }
}