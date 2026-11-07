package io.microconfig.plugin.greeter.impl

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "MicroconfigSettings", storages = [Storage("microconfig.xml")])
class MicroconfigSettings : PersistentStateComponent<MicroconfigSettings> {
    var version: String = "0.0.0"

    override fun getState(): MicroconfigSettings = this

    override fun loadState(state: MicroconfigSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
