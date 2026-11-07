package io.microconfig.plugin.actions.preview.impl

import com.intellij.openapi.actionSystem.CommonShortcuts.ESCAPE
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ScrollType.MAKE_VISIBLE
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.EditorTextField
import io.microconfig.core.properties.ConfigFormat.YAML
import io.microconfig.plugin.microconfig.ConfigOutput
import io.microconfig.plugin.microconfig.ConfigOutput.Companion.getFileType
import io.microconfig.plugin.microconfig.MicroconfigApi
import io.microconfig.plugin.microconfig.PluginContext
import java.awt.event.ActionListener
import javax.swing.Action
import javax.swing.DefaultComboBoxModel
import javax.swing.GroupLayout
import javax.swing.GroupLayout.Alignment.BASELINE
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

internal class PreviewDialog(private val context: PluginContext, private val api: MicroconfigApi) : DialogWrapper(context.project) {
    private val envsComboBox = ComboBox<String>()
    private val previewText = PreviewText(EditorFactory.getInstance().createDocument(""), context.project, getFileType(YAML))
    private val envPane = createEnvironmentPanel()

    init {
        updatePreviewText()
        closeOnEscapeIn(previewText)
    }

    public override fun createCenterPanel(): JComponent = previewText

    public override fun createNorthPanel(): JComponent = envPane

    override fun createActions(): Array<Action> = emptyArray()

    private fun createEnvironmentPanel(): JComponent {
        configureEnvironments()
        val listener = ActionListener { rebuildPreview() }
        envsComboBox.addActionListener(listener)
        val label = JLabel("Environment: ")
        val build = JButton("Build").apply { addActionListener(listener) }
        return environmentPanel(label, build)
    }

    private fun configureEnvironments() {
        val envs = api.getEnvs(context.projectDir()).filter { it.isNotEmpty() }
        envsComboBox.model = DefaultComboBoxModel(envs.toTypedArray())
        envsComboBox.isEditable = true
        envsComboBox.selectedItem = api.detectEnvOr(context.currentFile())
            ?: lastEnv.takeIf { it.isNotEmpty() && it in envs }
            ?: envs.firstOrNull().orEmpty()
    }

    private fun environmentPanel(label: JLabel, build: JButton): JPanel {
        val panel = JPanel()
        val layout = GroupLayout(panel)
        panel.layout = layout
        layout.autoCreateGaps = true
        layout.autoCreateContainerGaps = true
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(label).addComponent(envsComboBox).addComponent(build))
        layout.setVerticalGroup(layout.createParallelGroup(BASELINE).addComponent(label).addComponent(envsComboBox).addComponent(build))
        return panel
    }

    private fun rebuildPreview() {
        lastEnv = selectedEnvironment()
        val previousSize = size
        updatePreviewText()
        previousSize?.let { setSize(it.width, it.height) }
    }

    private fun selectedEnvironment(): String = envsComboBox.selectedItem as? String ?: ""

    private fun updatePreviewText() {
        val env = selectedEnvironment()
        val file = context.currentFile()
        title = "${file.parentFile.name}/${file.name}[$env] result configuration"
        val output = buildConfigs(env)
        previewText.text = output.text
        previewText.setFileType(output.fileType())
        moveToTop()
    }

    private fun buildConfigs(env: String): ConfigOutput =
        try {
            api.buildConfigs(context.currentFile(), context.projectDir(), env)
        } catch (error: RuntimeException) {
            ConfigOutput(YAML, error.message ?: error.javaClass.simpleName)
        }

    private fun moveToTop() {
        previewText.setCaretPosition(0)
        previewText.editor?.scrollingModel?.scrollToCaret(MAKE_VISIBLE)
    }

    private fun closeOnEscapeIn(component: JComponent) {
        DumbAwareAction.create { doCancelAction() }.registerCustomShortcutSet(ESCAPE, component, disposable)
    }

    private class PreviewText(document: Document, project: Project, fileType: FileType) :
        EditorTextField(document, project, fileType, true, false) {
        override fun createEditor(): EditorEx = super.createEditor().apply {
            setVerticalScrollbarVisible(true)
            setHorizontalScrollbarVisible(true)
        }
    }

    companion object {
        @Volatile private var lastEnv = ""

        fun create(context: PluginContext, api: MicroconfigApi) {
            val dialog = PreviewDialog(context, api)
            dialog.init()
            dialog.show()
        }
    }
}
