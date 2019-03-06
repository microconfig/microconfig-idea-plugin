package io.microconfig.plugin.actions.common;

import lombok.Data;

import java.io.File;

@Data
public class FilePosition {
    private final File file;
    private final int lineNumber;
}
