package io.microconfig.plugin.microconfig;

import io.microconfig.configs.io.ioservice.selector.FileFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConfigOutput {
    private final FileFormat format;
    private final String text;
}
