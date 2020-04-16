package io.microconfig.plugin.microconfig;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import io.microconfig.core.properties.ConfigFormat;
import lombok.Getter;

public class ConfigOutput {
    private final ConfigFormat configFormat;
    @Getter
    private final String text;

    public ConfigOutput(ConfigFormat configFormat, String text) {
        this.configFormat = configFormat;
        this.text = text.replaceAll("\r\n", "\n");
    }

    public FileType fileType() {
        return getFileType(configFormat);
    }

    public static FileType getFileType(ConfigFormat format) {
        return FileTypeManager.getInstance().getFileTypeByExtension(format.name().toLowerCase());
    }
}