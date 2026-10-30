package io.microconfig.plugin.greeter;

import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import com.intellij.testFramework.PlatformTestUtil;
import io.microconfig.plugin.MicroconfigRepoTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterTest extends MicroconfigRepoTestCase {
    @Test
    void notifiesAboutPluginUpdate() {
        List<Notification> notifications = subscribeToNotifications();
        getApplication().getService(MicroconfigSettings.class).setVersion("0.0.0");

        new Greeter().greet(project());
        PlatformTestUtil.dispatchAllEventsInIdeEventQueue();

        assertEquals(1, notifications.size());
        assertEquals("Microconfig plugin updated", notifications.get(0).getTitle());
        assertEquals(2, notifications.get(0).getActions().size());
    }

    private List<Notification> subscribeToNotifications() {
        List<Notification> notifications = new ArrayList<>();
        project().getMessageBus().connect(fixture.getTestRootDisposable()).subscribe(Notifications.TOPIC, new Notifications() {
            @Override
            public void notify(@NotNull Notification notification) {
                notifications.add(notification);
            }
        });
        return notifications;
    }
}
