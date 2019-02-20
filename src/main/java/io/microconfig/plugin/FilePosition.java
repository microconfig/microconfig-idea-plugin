package io.microconfig.plugin;

import lombok.Data;

import java.io.File;

@Data
public class FilePosition {
    private final File file;
    private final int lineNumber;
}
