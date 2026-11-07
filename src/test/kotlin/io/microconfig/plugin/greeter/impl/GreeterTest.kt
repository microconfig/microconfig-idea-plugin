package io.microconfig.plugin.greeter.impl

import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.testFramework.PlatformTestUtil
import io.microconfig.plugin.MicroconfigRepoTestCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GreeterTest : MicroconfigRepoTestCase() {
    @Test
    fun notifiesAboutPluginUpdate() {
        val notifications = subscribeToNotifications()
        getApplication().getService(MicroconfigSettings::class.java).version = "0.0.0"
        Greeter().greet(project())
        PlatformTestUtil.dispatchAllEventsInIdeEventQueue()
        assertEquals(1, notifications.size)
        assertEquals("Microconfig plugin updated", notifications.single().title)
        assertEquals(1, notifications.single().actions.size)
        assertEquals("Microconfig.IO", notifications.single().actions.single().templatePresentation.text)
        val expectedVersion = checkNotNull(javaClass.getResourceAsStream("/io/microconfig/plugin/greeter/version.txt"))
            .bufferedReader().use { it.readText().trim() }
        assertEquals(expectedVersion, getApplication().getService(MicroconfigSettings::class.java).version)
    }

    @Test
    fun doesNotNotifyTwiceForSameVersion() {
        val notifications = subscribeToNotifications()
        getApplication().getService(MicroconfigSettings::class.java).version = "0.0.0"
        val greeter = Greeter()
        greeter.greet(project())
        greeter.greet(project())
        PlatformTestUtil.dispatchAllEventsInIdeEventQueue()
        assertEquals(1, notifications.size)
    }

    private fun subscribeToNotifications(): List<Notification> {
        val notifications = mutableListOf<Notification>()
        project().messageBus.connect(fixture.testRootDisposable).subscribe(Notifications.TOPIC, object : Notifications {
            override fun notify(notification: Notification) {
                notifications.add(notification)
            }
        })
        return notifications
    }
}
