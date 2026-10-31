package io.microconfig.plugin.greeter;

import com.intellij.notification.BrowseNotificationAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static com.intellij.notification.NotificationType.INFORMATION;
import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Greeter implements ProjectActivity {
    private static final String displayId = "io.microconfig.plugin";
    private static final String message = "<br/>Thank you for using <b>Microconfig.IO</b>!<br/>";
    private static final String siteUrl = "https://microconfig.io";

    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        greet(project);
        return Unit.INSTANCE;
    }

    void greet(Project project) {
        var settings = getApplication().getService(MicroconfigSettings.class);
        var version = pluginVersion();

        if (!version.equals(settings.getVersion())) {
            settings.setVersion(version);
            Notification notification = createNotification();
            notification.notify(project);
        }
    }

    private String pluginVersion() {
        try (InputStream version = getClass().getResourceAsStream("version.txt")) {
            return new String(version.readAllBytes(), UTF_8).trim();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Notification createNotification() {
        return NotificationGroupManager.getInstance()
                .getNotificationGroup(displayId)
                .createNotification("Microconfig plugin updated", message, INFORMATION)
                .addAction(new BrowseNotificationAction("Microconfig.IO", siteUrl));
    }
}
