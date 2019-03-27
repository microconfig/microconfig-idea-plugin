package io.microconfig.plugin.actions.handler;

public class PluginException extends RuntimeException {
    public PluginException() {
    }

    public PluginException(String s) {
        super(s);
    }

    public PluginException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PluginException(Throwable throwable) {
        super(throwable);
    }

    public PluginException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
