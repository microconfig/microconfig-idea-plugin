package io.microconfig.plugin.actions.preview.impl

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.CommonShortcuts.ESCAPE
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.ui.DialogWrapper.CANCEL_EXIT_CODE
import com.intellij.testFramework.TestActionEvent
import com.intellij.ui.EditorTextField
import com.intellij.ui.UiInterceptors
import io.microconfig.core.properties.ConfigFormat.PROPERTIES
import io.microconfig.core.properties.ConfigFormat.YAML
import io.microconfig.plugin.MicroconfigRepoTestCase
import io.microconfig.plugin.microconfig.ConfigOutput
import io.microconfig.plugin.microconfig.MicroconfigApi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JComponent

class PreviewDialogTest : MicroconfigRepoTestCase() {
    @Test
    fun defaultsToFirstAvailableEnvironment() {
        assertInitialEnvironment(listOf("dev", "prod"), null, "dev")
    }

    @Test
    fun omitsEmptyEnvironmentFromOptions() {
        assertInitialEnvironment(listOf("", "dev", "prod"), null, "dev")
    }

    @Test
    fun usesBlankWhenNoEnvironmentsAreAvailable() {
        assertInitialEnvironment(emptyList(), null, "")
    }

    @Test
    fun prefersDetectedEnvironment() {
        assertInitialEnvironment(listOf("dev", "prod"), "prod", "prod")
    }

    @Test
    fun escapeInPreviewClosesDialog() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:")
        val dialog = PreviewDialog(editorContext(), MicroconfigApi.create())
        val escape = escapeActionOf(dialog.createCenterPanel())
        escape.actionPerformed(TestActionEvent.createTestEvent(escape))
        assertTrue(dialog.isDisposed)
        assertEquals(CANCEL_EXIT_CODE, dialog.exitCode)
    }

    @Test
    fun previewActionOpensAndInitializesDialog() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:")
        var opened: PreviewDialog? = null
        UiInterceptors.registerPossible(fixture.testRootDisposable, object : UiInterceptors.UiInterceptor<PreviewDialog>(PreviewDialog::class.java) {
            override fun doIntercept(component: PreviewDialog) {
                opened = component
                try {
                    assertTrue(previewOf(component).text.contains("http://localhost:"))
                    assertTrue(component.title.startsWith("payments/application.yaml["))
                } finally {
                    component.close(CANCEL_EXIT_CODE)
                }
            }
        })

        fixture.performEditorAction("Microconfig.PreviewAction")

        assertNotNull(opened, "Preview action must open the preview dialog")
    }

    @Test
    fun rebuildsWhenEnvironmentChangesAndUpdatesFileType() {
        prepareEditor()
        val api = mockApi(listOf("dev", "prod"), "dev")
        stubBuild(api, "dev", ConfigOutput(YAML, "mode: development\n"))
        stubBuild(api, "prod", ConfigOutput(PROPERTIES, "mode=production\n"))
        val dialog = PreviewDialog(editorContext(), api)
        try {
            assertEquals("mode: development\n", previewOf(dialog).text)

            environmentsOf(dialog).selectedItem = "prod"

            assertEquals("mode=production\n", previewOf(dialog).text)
            assertEquals(ConfigOutput.getFileType(PROPERTIES), previewOf(dialog).fileType)
            assertEquals("payments/application.yaml[prod] result configuration", dialog.title)
            verify(api).buildConfigs(editorContext().currentFile(), projectDir, "prod")
        } finally {
            dialog.close(CANCEL_EXIT_CODE)
        }
    }

    @Test
    fun buildButtonRefreshesCurrentEnvironment() {
        prepareEditor()
        val api = mockApi(listOf("dev"), "dev")
        val context = editorContext()
        `when`(api.buildConfigs(context.currentFile(), projectDir, "dev"))
            .thenReturn(ConfigOutput(YAML, "before"), ConfigOutput(YAML, "after"))
        val dialog = PreviewDialog(context, api)
        try {
            assertEquals("before", previewOf(dialog).text)

            buildButtonOf(dialog).doClick(0)

            assertEquals("after", previewOf(dialog).text)
            verify(api, times(2)).buildConfigs(context.currentFile(), projectDir, "dev")
        } finally {
            dialog.close(CANCEL_EXIT_CODE)
        }
    }

    @Test
    fun displaysBuildErrorAndCanRetry() {
        prepareEditor()
        val api = mockApi(listOf("dev"), "dev")
        val context = editorContext()
        `when`(api.buildConfigs(context.currentFile(), projectDir, "dev"))
            .thenThrow(IllegalStateException("Broken include"))
            .thenReturn(ConfigOutput(YAML, "recovered"))
        val dialog = PreviewDialog(context, api)
        try {
            assertEquals("Broken include", previewOf(dialog).text)

            buildButtonOf(dialog).doClick(0)

            assertEquals("recovered", previewOf(dialog).text)
        } finally {
            dialog.close(CANCEL_EXIT_CODE)
        }
    }

    @Test
    fun displaysExceptionTypeWhenBuildErrorHasNoMessage() {
        prepareEditor()
        val api = mockApi(listOf("dev"), "dev")
        `when`(api.buildConfigs(editorContext().currentFile(), projectDir, "dev")).thenThrow(IllegalStateException())
        val dialog = PreviewDialog(editorContext(), api)
        try {
            assertEquals("IllegalStateException", previewOf(dialog).text)
        } finally {
            dialog.close(CANCEL_EXIT_CODE)
        }
    }

    @Test
    fun allowsTypedEnvironmentOutsideAvailableOptions() {
        prepareEditor()
        val api = mockApi(listOf("dev"), "dev")
        stubBuild(api, "dev", ConfigOutput(YAML, "development"))
        stubBuild(api, "custom", ConfigOutput(YAML, "custom preview"))
        val dialog = PreviewDialog(editorContext(), api)
        try {
            environmentsOf(dialog).selectedItem = "custom"

            assertEquals("custom preview", previewOf(dialog).text)
            assertEquals("payments/application.yaml[custom] result configuration", dialog.title)
        } finally {
            dialog.close(CANCEL_EXIT_CODE)
        }
    }

    @Test
    fun remembersSelectedEnvironmentAcrossDialogs() {
        prepareEditor()
        rememberEnvironment("prod")

        assertInitialEnvironment(listOf("dev", "prod"), null, "prod")
    }

    @Test
    fun detectedEnvironmentTakesPrecedenceOverRememberedEnvironment() {
        prepareEditor()
        rememberEnvironment("prod")

        assertInitialEnvironment(listOf("dev", "prod"), "dev", "dev")
    }

    @Test
    fun fallsBackWhenRememberedEnvironmentIsUnavailable() {
        prepareEditor()
        rememberEnvironment("prod")

        assertInitialEnvironment(listOf("dev"), null, "dev")
    }

    @AfterEach
    fun clearRememberedEnvironment() {
        prepareEditor()
        rememberEnvironment("")
    }

    private fun assertInitialEnvironment(envs: List<String>, detectedEnv: String?, expectedEnv: String) {
        prepareEditor()
        val context = editorContext()
        val api = mockApi(envs, detectedEnv)
        stubBuild(api, expectedEnv, ConfigOutput(YAML, "preview"))
        val dialog = PreviewDialog(context, api)
        try {
            val comboBox = environmentsOf(dialog)
            assertEquals(expectedEnv, comboBox.selectedItem)
            val expectedOptions = envs.filter { it.isNotEmpty() }
            assertEquals(expectedOptions, (0 until comboBox.itemCount).map { comboBox.getItemAt(it) })
            verify(api).buildConfigs(context.currentFile(), context.projectDir(), expectedEnv)
        } finally {
            dialog.close(CANCEL_EXIT_CODE)
        }
    }

    private fun prepareEditor() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:")
    }

    private fun mockApi(envs: List<String>, detectedEnv: String?): MicroconfigApi {
        val api = mock(MicroconfigApi::class.java)
        `when`(api.getEnvs(projectDir)).thenReturn(envs.toSet())
        `when`(api.detectEnvOr(editorContext().currentFile())).thenReturn(detectedEnv)
        return api
    }

    private fun stubBuild(api: MicroconfigApi, env: String, output: ConfigOutput) {
        `when`(api.buildConfigs(editorContext().currentFile(), projectDir, env)).thenReturn(output)
    }

    private fun environmentsOf(dialog: PreviewDialog): JComboBox<*> =
        dialog.createNorthPanel().components.filterIsInstance<JComboBox<*>>().single()

    private fun escapeActionOf(component: JComponent): AnAction =
        ActionUtil.getActions(component).firstOrNull { it.shortcutSet.shortcuts.contentEquals(ESCAPE.shortcuts) }
            ?: throw AssertionError("Escape isn't bound in preview")

    private fun previewOf(dialog: PreviewDialog): EditorTextField = dialog.createCenterPanel() as EditorTextField

    private fun buildButtonOf(dialog: PreviewDialog): JButton =
        dialog.createNorthPanel().components.filterIsInstance<JButton>().single()

    private fun rememberEnvironment(env: String) {
        val api = mockApi(if (env.isEmpty()) emptyList() else listOf(env), env)
        stubBuild(api, env, ConfigOutput(YAML, "preview"))
        val dialog = PreviewDialog(editorContext(), api)
        try {
            buildButtonOf(dialog).doClick(0)
        } finally {
            dialog.close(CANCEL_EXIT_CODE)
        }
    }
}
