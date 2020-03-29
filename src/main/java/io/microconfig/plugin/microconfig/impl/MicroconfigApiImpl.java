package io.microconfig.plugin.microconfig.impl;

import io.microconfig.core.Microconfig;
import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.configtypes.ConfigTypeImpl;
import io.microconfig.core.properties.*;
import io.microconfig.core.properties.repository.ConfigFile;
import io.microconfig.core.properties.repository.ConfigFileRepository;
import io.microconfig.core.properties.repository.Include;
import io.microconfig.core.properties.repository.Includes;
import io.microconfig.core.properties.resolvers.placeholder.Placeholder;
import io.microconfig.plugin.microconfig.ConfigOutput;
import io.microconfig.plugin.microconfig.FilePosition;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.MicroconfigInitializer;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static io.microconfig.core.configtypes.ConfigTypeFilters.configTypeWithExtensionOf;
import static io.microconfig.core.configtypes.StandardConfigType.APPLICATION;
import static io.microconfig.core.properties.ConfigFormat.PROPERTIES;
import static io.microconfig.core.properties.ConfigFormat.YAML;
import static io.microconfig.core.properties.resolvers.placeholder.PlaceholderBorders.findPlaceholderIn;
import static io.microconfig.core.properties.serializers.PropertySerializers.asString;
import static io.microconfig.utils.StringUtils.dotCountIn;
import static java.lang.Math.max;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public class MicroconfigApiImpl implements MicroconfigApi {
    private final MicroconfigInitializer initializer = new MicroconfigInitializerImpl();

    @Override
    public File findIncludeSource(String includeLine, int currentColumn, File currentFile, File projectDir) {
        Supplier<Include> parseInclude = () -> {
            List<Include> includes = Includes.from(includeLine).withDefaultEnv(detectEnvOr(currentFile).orElse(""));
            if (includes.size() == 1) return includes.get(0);

            int componentIndex = getIncludedComponentIndex(includeLine, currentColumn);
            return includes.get(componentIndex);
        };

        Include include = parseInclude.get();
        return findSourceFile(include.getComponent(), include.getEnvironment(), currentFile, initializer.getMicroconfig(projectDir));
    }

    @Override
    public FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir) {
        Placeholder placeholder = parsePlaceholder(placeholderValue, currentFile);

        Microconfig microconfig = initializer.getMicroconfig(projectDir);
        Optional<Property> resolved = microconfig
                .environments()
                .getOrCreateByName(placeholder.getEnvironment())
                .getOrCreateComponentWithName(placeholder.getComponent())
                .getPropertiesFor(configTypeWithExtensionOf(currentFile))
                .getPropertyWithKey(placeholder.getKey());

        return resolved.map(Property::getDeclaringComponent)
                .filter(dc -> dc instanceof FileBasedComponent)
                .map(FileBasedComponent.class::cast)
                .map(FilePosition::positionFromFileSource)
                .orElseGet(() -> {
                    File sourceFile = findSourceFile(placeholder.getComponent(), placeholder.getEnvironment(), currentFile, microconfig);
                    return new FilePosition(sourceFile, 0);
                });
    }

    private Placeholder parsePlaceholder(String placeholderValue, File currentFile) {
        Placeholder placeholder = findPlaceholderIn(placeholderValue)
                .orElseThrow(() -> new IllegalStateException("Can't parse " + placeholderValue))
                .toPlaceholder("some", detectEnvOr(currentFile).orElse(""));
        return placeholder.isSelfReferenced() ? placeholder.withComponent(currentFile.getParentFile().getName()) : placeholder;
    }

    private File findSourceFile(String component, String env, File currentFile, Microconfig microconfig) {
        List<ConfigFile> configFiles = microconfig.getDependencies()
                .getConfigFileRepository()
                .getConfigFilesOf(component, env, configTypeOf(currentFile, microconfig));
        return configFiles.get(configFiles.size() - 1).getFile();
    }

    @Override
    public Map<String, String> resolvePlaceholderForEachEnv(String placeholderValue, File currentFile, File projectDir) {
        return resolveFullLineForEachEnv("key=" + placeholderValue, currentFile, projectDir);
    }

    @Override
    public Map<String, String> resolveFullLineForEachEnv(String currentLine, File currentFile, File projectDir) {
        String component = currentFile.getParentFile().getName();
        Microconfig microconfig = initializer.getMicroconfig(projectDir);
        String configType = configTypeOf(currentFile, microconfig).getName();
        Resolver resolver = microconfig.resolver();
        UnaryOperator<String> resolveValue = env -> {
            try {
                DeclaringComponent root = new DeclaringComponentImpl(configType, component, env);
                return resolver.resolve(currentLine, root, root);
            } catch (RuntimeException e) {
                return "ERROR";
            }
        };

        return envsFor(currentLine, currentFile, microconfig)
                .collect(toMap(identity(), resolveValue, (k1, k2) -> k1, TreeMap::new));
    }

    @Override
    public File findAnyComponentFile(String component, String env, File projectDir) {
        Microconfig microconfig = initializer.getMicroconfig(projectDir);
        ConfigFileRepository configFileRepository = microconfig.getDependencies().getConfigFileRepository();
        List<ConfigFile> configFiles = configFileRepository.getConfigFilesOf(component, env, APPLICATION);
        if (configFiles.isEmpty()) {
            configFiles = configFileRepository.getConfigFilesOf(component, env, compositeConfigType(microconfig));
        }
        return configFiles.get(configFiles.size() - 1).getFile();
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
                properties.getProperties().stream().anyMatch(p -> p.getConfigFormat() == YAML) ? YAML : PROPERTIES,
                properties.save(asString())
        );
    }

    @Override
    public Set<String> getEnvs(File projectDir) {
        return initializer.getMicroconfig(projectDir)
                .environments()
                .environmentNames();
    }

    @Override
    public Optional<String> detectEnvOr(File currentFile) {
        String name = currentFile.getName();
        int start = name.indexOf('.');
        int end = name.indexOf('.', start + 1);
        return end > 0 ? Optional.of(name.substring(start + 1, end)) : Optional.empty();
    }

    private Stream<String> envsFor(String currentLine, File currentFile, Microconfig microconfig) {
        if (!findPlaceholderIn(currentLine).isPresent()) return of("");

        if (isEnvSpecificConfig(currentFile)) {
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

    private boolean isEnvSpecificConfig(File currentFile) {
        return dotCountIn(currentFile.getName()) > 1;
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

    private ConfigType compositeConfigType(Microconfig microconfig) {
        Set<String> allConfigTypeExtensions = microconfig.getDependencies()
                .getConfigTypeRepository()
                .getConfigTypes().stream()
                .flatMap(ct -> ct.getSourceExtensions().stream())
                .collect(toSet());
        return new ConfigTypeImpl("fake", allConfigTypeExtensions, "fake");
    }

    private ConfigType configTypeOf(File currentFile, Microconfig microconfig) {
        List<ConfigType> supportedConfigTypes = microconfig.getDependencies()
                .getConfigTypeRepository()
                .getConfigTypes();

        return configTypeWithExtensionOf(currentFile)
                .selectTypes(supportedConfigTypes)
                .get(0);
    }
}