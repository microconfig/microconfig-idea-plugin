package io.microconfig.plugin.microconfig

import io.microconfig.core.properties.FileBasedComponent
import java.io.File

class FilePosition(private val file: File, private val lineNumber: Int) {
    fun moveToPosition(context: PluginContext) {
        context.navigateTo(file, lineNumber)
    }

    companion object {
        fun positionFromFileSource(source: FileBasedComponent): FilePosition =
            FilePosition(source.source, source.lineNumber)
    }
}
