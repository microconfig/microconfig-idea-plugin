package io.microconfig.plugin.microconfig;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import io.microconfig.core.properties.io.ioservice.selector.FileFormat;
import lombok.Getter;

public class ConfigOutput {
    private final FileFormat format;
    @Getter
    private final String text;

    public ConfigOutput(FileFormat format, String text) {
        this.format = format;
        this.text = text.replaceAll("\r\n", "\n");
    }

    public FileType fileType() {
        return getFileType(format);
    }

    public static FileType getFileType(FileFormat format) {
        return FileTypeManager.getInstance().getFileTypeByExtension(format.name().toLowerCase());
    }
}