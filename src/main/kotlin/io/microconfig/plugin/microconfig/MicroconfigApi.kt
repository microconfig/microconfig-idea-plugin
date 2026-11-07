package io.microconfig.plugin.microconfig

import io.microconfig.plugin.microconfig.impl.MicroconfigApiImpl
import java.io.File

interface MicroconfigApi {
    fun findIncludeSource(includeLine: String, currentColumn: Int, currentFile: File, projectDir: File): File

    fun findAnyComponentFile(component: String, env: String, projectDir: File): File

    fun findPlaceholderSource(placeholderValue: String, currentFile: File, projectDir: File): FilePosition

    fun resolvePlaceholderForEachEnv(placeholderValue: String, currentFile: File, projectDir: File): Map<String, String>

    fun resolveFullLineForEachEnv(currentLine: String, currentFile: File, projectDir: File): Map<String, String>

    fun detectEnvOr(currentFile: File): String?

    fun getEnvs(projectDir: File): Set<String>

    fun buildConfigs(currentFile: File, projectDir: File, env: String): ConfigOutput

    companion object {
        fun create(): MicroconfigApi = MicroconfigApiImpl()
    }
}
