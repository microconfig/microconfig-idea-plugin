package io.microconfig.plugin.run.impl

import com.intellij.openapi.options.SettingsEditor
import javax.swing.GroupLayout
import javax.swing.GroupLayout.Alignment.BASELINE
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingConstants.HORIZONTAL
import javax.swing.SwingConstants.RIGHT

class RunConfigEditor : SettingsEditor<RunConfig>() {
    private val envText = JTextField(20)
    private val groupText = JTextField(20)
    private val servicesText = JTextField(20)
    private val destinationText = JTextField(20)
    private val panel = createPanel()

    override fun createEditor(): JComponent = panel

    override fun resetEditorFrom(config: RunConfig) {
        envText.text = config.env
        groupText.text = config.groups
        servicesText.text = config.services
        destinationText.text = config.destination
    }

    override fun applyEditorTo(config: RunConfig) {
        config.env = envText.text
        config.groups = groupText.text
        config.services = servicesText.text
        config.destination = destinationText.text
    }

    private fun createPanel(): JPanel {
        val panel = JPanel()
        val layout = GroupLayout(panel)
        panel.layout = layout
        layout.autoCreateGaps = true
        layout.autoCreateContainerGaps = true
        addFields(layout)
        return panel
    }

    private fun addFields(layout: GroupLayout) {
        val horizontal = layout.createParallelGroup()
        val vertical = layout.createSequentialGroup()
        val fields = listOf("Environment: " to envText, "Groups: " to groupText, "Services: " to servicesText, "Destination: " to destinationText)
        val labels = fields.map { (name, field) ->
            val label = JLabel(name, RIGHT).apply { labelFor = field }
            horizontal.addGroup(layout.createSequentialGroup().addComponent(label).addComponent(field))
            vertical.addGroup(layout.createParallelGroup(BASELINE).addComponent(label).addComponent(field))
            label
        }
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(horizontal))
        layout.setVerticalGroup(vertical)
        layout.linkSize(HORIZONTAL, *labels.toTypedArray())
    }
}
