package io.microconfig.plugin.components;

import io.microconfig.plugin.FileFinder;
import io.microconfig.plugin.PluginContext;

import static io.microconfig.plugin.ContextUtils.componentType;

public class IncludeComponent implements MicroconfigComponent {
     static final String INCLUDE = "#include";

    private final FileFinder fileFinder = new FileFinder();

    private final PluginContext context;
    private final String currentLine;

    public IncludeComponent(PluginContext context, String currentLine) {
        this.context = context;
        this.currentLine = currentLine;
    }

    @Override
    public void react() {
         fileFinder.resolveComponent(context.project, componentName(currentLine))
            .flatMap(dir -> fileFinder.findComponentFile(context.project, dir, componentType(context)))
            .ifPresent(f -> f.navigate(true));
    }

    private String componentName(String currentLine) {
        return currentLine.substring(INCLUDE.length()).trim();
    }


}
