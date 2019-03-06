package io.microconfig.plugin.microconfig;

import io.microconfig.commands.buildconfig.factory.MicroconfigFactory;
import io.microconfig.configs.Property;
import io.microconfig.configs.PropertySource;
import io.microconfig.configs.provider.Include;
import io.microconfig.configs.resolver.EnvComponent;
import io.microconfig.configs.resolver.PropertyResolver;
import io.microconfig.configs.resolver.PropertyResolverHolder;
import io.microconfig.configs.resolver.placeholder.Placeholder;
import io.microconfig.plugin.actions.common.FilePosition;
import io.microconfig.plugin.actions.common.PluginException;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static io.microconfig.configs.Property.parse;
import static io.microconfig.configs.PropertySource.fileSource;
import static io.microconfig.environments.Component.byType;
import static io.microconfig.plugin.utils.FileUtil.fileExtension;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

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
        String fileExtension = fileExtension(currentFile);
        return initializer.getMicroconfigFactory(projectDir)
                .getComponentTree()
                .getConfigFiles(componentName, file -> file.getName().endsWith(fileExtension))
                .min(priorityByEnv.get())
                .orElseThrow(() -> new PluginException("Component not found: " + componentName));
    }

    @Override
    public FilePosition findPlaceholderSource(String placeholderValue, File currentFile, File projectDir) {
        MicroconfigFactory factory = initializer.getMicroconfigFactory(projectDir);

        Placeholder p = toPlaceholder(placeholderValue, currentFile, anyEnv(factory));
        Property property = factory
                .newConfigProvider(initializer.detectConfigType(currentFile))
                .getProperties(byType(p.getComponent()), p.getEnvironment())
                .get(p.getValue());
        if (property == null) {
            throw new PluginException("Can't resolve " + placeholderValue); //todo return FilePosition(componentFile, 0)
        }

        PropertySource source = property.getSource();
        return new FilePosition(new File(source.getSourceOfProperty()), source.getLine());
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

    private Placeholder toPlaceholder(String placeholderValue, File currentFile, String env) {
        Placeholder p = Placeholder.parse(placeholderValue, env);
        return p.isSelfReferenced() ? p.changeComponent(currentFile.getParentFile().getName()) : p;
    }

    private String anyEnv(MicroconfigFactory factory) {
        return factory.getEnvironmentProvider()
                .getEnvironmentNames()
                .stream()
                .findFirst()
                .orElse(""); //otherwise will fail for env-specific props
    }
}