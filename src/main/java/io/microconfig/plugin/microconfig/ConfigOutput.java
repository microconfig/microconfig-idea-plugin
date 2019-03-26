package io.microconfig.plugin.microconfig;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import io.microconfig.configs.io.ioservice.selector.FileFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ConfigOutput {
    private final FileFormat format;
    @Getter
    private final String text;

    public FileType fileType() {
        return getFileType(format);
    }


    public static FileType getFileType(FileFormat format) {
        return FileTypeManager.getInstance().getFileTypeByExtension(format.name().toLowerCase());
    }
}