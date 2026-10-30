package io.microconfig.plugin.microconfig.impl;

import io.microconfig.plugin.MicroconfigRepoTestCase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VirtualFileReaderTest extends MicroconfigRepoTestCase {
    @Test
    void readsFileUnknownToVfsFromDisk() throws Exception {
        File file = repoFile("components/eureka/application.dev.yaml");
        toVirtualFile(file.getParentFile()).getChildren();
        Files.writeString(file.toPath(), "server:\n  port: 7761\n");
        assertNull(toVirtualFile(file));

        assertEquals("server:\n  port: 7761\n", new VirtualFileReader().readFully(file));
    }
}
