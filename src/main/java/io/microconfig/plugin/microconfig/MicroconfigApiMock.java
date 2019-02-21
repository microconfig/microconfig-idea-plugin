package io.microconfig.plugin.microconfig;

import io.microconfig.commands.factory.MicroconfigFactory;
import io.microconfig.plugin.FilePosition;
import io.microconfig.plugin.PluginException;
import io.microconfig.properties.Property;
import io.microconfig.properties.Property.Source;
import io.microconfig.properties.files.parser.Include;
import io.microconfig.properties.resolver.placeholder.Placeholder;

import java.io.File;
import java.util.Map;

import static io.microconfig.commands.factory.ConfigType.byExtension;
import static io.microconfig.environments.Component.byType;
import static io.microconfig.plugin.utils.ContextUtils.fileExtension;
import static java.util.Comparator.comparing;

public class MicroconfigApiMock implements MicroconfigApi {
    @Override
    public File findInclude(File projectDir, String includeLine, String currentFileName) {
        Include include = Include.parse(includeLine, "BASE");

        String componentName = include.getComponentName();
        String fileExtension = fileExtension(currentFileName);

        return microconfigFactory(projectDir)
                .getComponentTree()
                .getConfigFiles(componentName, file -> file.getName().endsWith(fileExtension))
                .min(comparing(f -> f.getName().length()))
                .orElseThrow(() -> new PluginException("Component not found: " + componentName));
    }

    @Override
    public FilePosition findPlaceholderKey(File projectDir, String placeholder, String currentFileName) {
        Placeholder p = Placeholder.parse(placeholder, "BASE");

        Property property = microconfigFactory(projectDir)
                .newPropertiesProvider(byExtension(fileExtension(currentFileName)))
                .getProperties(byType(p.getComponent()), p.getEnvironment())
                .get(p.getValue());

        if (property == null) {
            throw new PluginException("Can't resolve " + placeholder);
        }

        Source source = property.getSource();
        return new FilePosition(new File(source.getSourceOfProperty()), source.getLine());
    }

    @Override
    public Map<String, String> placeholderValues(File projectDir, String currentLine) {
        return null;
    }

    @Override
    public boolean navigatable(String placeholder) {
        return true;
    }

    @Override
    public boolean insidePlaceholder(String line, int offset) {
        return true;
    }

    private MicroconfigFactory microconfigFactory(File projectDir) {
        return MicroconfigFactory.init(
                new File(projectDir, "repo"), //todo
                new File(projectDir, "build/output")
        );
    }
}