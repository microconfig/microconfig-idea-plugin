package io.microconfig.plugin.microconfig.impl;

import io.microconfig.plugin.MicroconfigRepoTestCase;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroconfigApiImplTest extends MicroconfigRepoTestCase {
    private final MicroconfigApi api = new MicroconfigApiImpl();

    @Test
    void resolvePlaceholderForEachEnv() {
        Map<String, String> valueByEnv = api.resolvePlaceholderForEachEnv(
                "${eureka@server.port}", repoFile("components/payments/application.yaml"), projectDir);

        assertEquals("8761", valueByEnv.get("dev"));
        assertEquals("9761", valueByEnv.get("prod"));
    }

    @Test
    void buildConfigsForPreview() {
        String text = api.buildConfigs(repoFile("components/payments/application.yaml"), projectDir, "prod").getText();

        assertTrue(text.contains("http://localhost:9761"), text);
    }

    @Test
    void componentWithoutConfigFiles() {
        var e = assertThrows(IllegalStateException.class, () -> api.findAnyComponentFile("empty", "dev", projectDir));

        assertEquals("Can't find config files of component 'empty'", e.getMessage());
    }
}
