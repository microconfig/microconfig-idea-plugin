package io.microconfig.plugin.microconfig;

import io.microconfig.core.properties.FileBasedComponent;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class FilePosition {
    private final File file;
    private final int lineNumber;

    public static FilePosition positionFromFileSource(FileBasedComponent source) {
        return new FilePosition(
                source.getSource(),
                source.getLineNumber()
        );
    }

    public void moveToPosition(PluginContext context) {
        context.navigateTo(file, lineNumber);
    }
}