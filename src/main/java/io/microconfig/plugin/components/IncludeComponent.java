package io.microconfig.plugin.components;

import io.microconfig.plugin.FileFinder;
import io.microconfig.plugin.PluginContext;

import static io.microconfig.plugin.ContextUtils.componentType;

public class IncludeComponent implements MicroconfigComponent {
    static final String INCLUDE = "#include";

    private static final FileFinder FILE_FINDER = new FileFinder();

    private final PluginContext context;
    private final String currentLine;

    IncludeComponent(PluginContext context, String currentLine) {
        this.context = context;
        this.currentLine = currentLine;
    }

    public static boolean hasIncludeTag(String currentLine) {
        return currentLine.startsWith(INCLUDE);
    }

    @Override
    public void react() {
         FILE_FINDER.resolveComponent(context.project, componentName(currentLine))
            .flatMap(dir -> FILE_FINDER.findComponentFile(context.project, dir, componentType(context)))
            .ifPresent(f -> f.navigate(true));
    }

    private String componentName(String currentLine) {
        return currentLine.substring(INCLUDE.length()).trim();
    }


}
