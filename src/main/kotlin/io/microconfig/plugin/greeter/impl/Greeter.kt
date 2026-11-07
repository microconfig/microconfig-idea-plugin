package io.microconfig.plugin.greeter.impl

import com.intellij.notification.BrowseNotificationAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.io.IOException
import java.io.UncheckedIOException

class Greeter : ProjectActivity {
    override suspend fun execute(project: Project) {
        greet(project)
    }

    internal fun greet(project: Project) {
        val settings = getApplication().getService(MicroconfigSettings::class.java)
        val version = pluginVersion()
        if (version == settings.version) return
        settings.version = version
        createNotification().notify(project)
    }

    private fun pluginVersion(): String =
        try {
            checkNotNull(javaClass.getResourceAsStream("/io/microconfig/plugin/greeter/version.txt"))
                .bufferedReader(Charsets.UTF_8).use { it.readText().trim() }
        } catch (error: IOException) {
            throw UncheckedIOException(error)
        }

    private fun createNotification(): Notification = NotificationGroupManager.getInstance()
        .getNotificationGroup("io.microconfig.plugin")
        .createNotification("Microconfig plugin updated", "<br/>Thank you for using <b>Microconfig.IO</b>!<br/>", INFORMATION)
        .addAction(BrowseNotificationAction("Microconfig.IO", "https://microconfig.io"))
}
