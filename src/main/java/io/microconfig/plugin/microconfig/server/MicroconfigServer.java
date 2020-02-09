package io.microconfig.plugin.microconfig.server;

import io.microconfig.plugin.microconfig.PluginContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MicroconfigServer {

    public void resolveVaultSecret(PluginContext context, String placeholder) {
        ServerConfig config = new ServerConfig();

        URI uri = requestUri(config, placeholder);
        if (uri == null) return;

        HttpGet request = new HttpGet(uri);
        request.addHeader("X-AUTH-TYPE", "VAULT_TOKEN");
        request.addHeader("X-VAULT-TOKEN", config.vaultToken());

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            String value = EntityUtils.toString(response.getEntity());
            context.showInfoHint(value);
        } catch (IOException e) {
            context.showErrorHint(e);
        }
    }

    private URI requestUri(ServerConfig config, String placeholder) {
        try {
            int atIdx = placeholder.indexOf('@');
            String secret = placeholder.substring(atIdx + 1, placeholder.length() - 1);
            return new URIBuilder(config.serverUrl())
                .setPath("/api/vault-secret")
                .setParameter("secret", secret)
                .build();
        } catch (URISyntaxException e) {
            //todo ignored
            return null;
        }
    }
}
