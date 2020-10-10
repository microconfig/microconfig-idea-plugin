package io.microconfig.plugin.greeter;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ide.plugins.PluginManagerCore.getPlugin;
import static com.intellij.ide.plugins.PluginManagerCore.isPluginInstalled;
import static com.intellij.notification.NotificationDisplayType.STICKY_BALLOON;
import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.INFORMATION;
import static com.intellij.openapi.components.ServiceManager.getService;

public class Greeter implements StartupActivity {
    private static final String displayId = "io.microconfig.plugin";
    private static final String message = "<br/>Thank you for using <b><a href=\"https://microconfig.io\">Microconfig.IO</a></b>!<br/>" +
            "If you have any questions or ideas you can join our <b><a href=\"https://join.slack.com/t/microconfig/shared_invite/zt-dflf2m0n-wOPVfAmk5eiHPn_9Omff7Q\">Slack</a></b>";

    @Override
    public void runActivity(@NotNull Project project) {
        var pluginId = PluginId.getId("io.microconfig.idea-plugin");
        if (!isPluginInstalled(pluginId)) return;

        var settings = getService(MicroconfigSettings.class);
        var descriptor = getPlugin(pluginId);

        if (descriptor != null && !descriptor.getVersion().equals(settings.getVersion())) {
            settings.setVersion(descriptor.getVersion());
            Notification notification = createNotification();
            notification.notify(project);
        }
    }

    private Notification createNotification() {
        NotificationGroup group = new NotificationGroup(displayId, STICKY_BALLOON, true);
        return group.createNotification("Microconfig plugin updated", message, INFORMATION, URL_OPENING_LISTENER);
    }
}