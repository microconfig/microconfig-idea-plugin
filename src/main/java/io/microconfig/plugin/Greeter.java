package io.microconfig.plugin;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.intellij.notification.NotificationDisplayType.STICKY_BALLOON;
import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.INFORMATION;
import static com.intellij.notification.impl.NotificationsManagerImpl.createBalloon;
import static com.intellij.ui.BalloonLayoutData.fullContent;

public class Greeter implements StartupActivity {

    private final String displayId = "io.microconfig.plugin";
    private final String message = "<br/>Thank you for using <b><a href=\"https://microconfig.io\">Microconfig.IO</a></b>!<br/>" +
            "If you have any questions or ideas you can join our <b><a href=\"https://join.slack.com/t/microconfig/shared_invite/zt-couuwwuo-vt5miXcv5RMmuXlNv1oKEQ\">Slack</a></b>";

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void runActivity(@NotNull Project project) {
        MicroconfigSettings settings = ServiceManager.getService(MicroconfigSettings.class);
        System.out.println("Settings version: "+ settings.getVersion());
        IdeaPluginDescriptor descriptor = PluginManager.getPlugin(PluginId.getId("io.microconfig.idea-plugin"));

        if (!descriptor.getVersion().equals(settings.version)) {
            settings.setVersion(descriptor.getVersion());
            Notification notification = createNotification();
            IdeFrame frame = WindowManager.getInstance().getIdeFrame(project);
            if (frame == null) {
                notification.notify(project);
                return;
            }
            Rectangle bounds = frame.getComponent().getBounds();
            RelativePoint target = new RelativePoint(frame.getComponent(), new Point(bounds.x + bounds.width, 20));

            try {
                Balloon balloon = createBalloon(
                        frame,
                        notification,
                        true, // showCallout
                        false, // hideOnClickOutside
                        fullContent(),
                        project);
                balloon.show(target, Balloon.Position.atLeft);
            } catch (Exception e) {
                notification.notify(project);
            }
        }
    }

    private Notification createNotification() {
        NotificationGroup group = new NotificationGroup(displayId, STICKY_BALLOON, true);
        return group.createNotification("Microconfig plugin updated", message, INFORMATION, URL_OPENING_LISTENER);
    }

}
