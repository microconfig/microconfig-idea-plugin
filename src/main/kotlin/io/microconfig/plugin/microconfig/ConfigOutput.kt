package io.microconfig.plugin.microconfig

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import io.microconfig.core.properties.ConfigFormat
import java.util.Locale

class ConfigOutput(private val configFormat: ConfigFormat, text: String) {
    val text: String = text.replace("\r\n", "\n")

    fun fileType(): FileType = getFileType(configFormat)

    companion object {
        fun getFileType(format: ConfigFormat): FileType =
            FileTypeManager.getInstance().getFileTypeByExtension(format.name.lowercase(Locale.ROOT))
    }
}
