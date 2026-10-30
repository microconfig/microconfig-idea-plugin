package io.microconfig.plugin.greeter;

import com.intellij.notification.BrowseNotificationAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ide.plugins.PluginManagerCore.getPlugin;
import static com.intellij.ide.plugins.PluginManagerCore.isPluginInstalled;
import static com.intellij.notification.NotificationType.INFORMATION;
import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class Greeter implements StartupActivity {
    private static final String displayId = "io.microconfig.plugin";
    private static final String message = "<br/>Thank you for using <b>Microconfig.IO</b>!<br/>" +
            "If you have any questions or ideas you can join our <b>Slack</b>";
    private static final String siteUrl = "https://microconfig.io";
    private static final String slackUrl = "https://join.slack.com/t/microconfig/shared_invite/zt-dflf2m0n-wOPVfAmk5eiHPn_9Omff7Q";

    @Override
    public void runActivity(@NotNull Project project) {
        var pluginId = PluginId.getId("io.microconfig.idea-plugin");
        if (!isPluginInstalled(pluginId)) return;

        var settings = getApplication().getService(MicroconfigSettings.class);
        var descriptor = getPlugin(pluginId);

        if (descriptor != null && !descriptor.getVersion().equals(settings.getVersion())) {
            settings.setVersion(descriptor.getVersion());
            Notification notification = createNotification();
            notification.notify(project);
        }
    }

    private Notification createNotification() {
        return NotificationGroupManager.getInstance()
                .getNotificationGroup(displayId)
                .createNotification("Microconfig plugin updated", message, INFORMATION)
                .addAction(new BrowseNotificationAction("Microconfig.IO", siteUrl))
                .addAction(new BrowseNotificationAction("Join Slack", slackUrl));
    }
}
