package io.microconfig.plugin.microconfig

import io.microconfig.core.Microconfig
import io.microconfig.plugin.microconfig.impl.MicroconfigInitializerImpl
import java.io.File

interface MicroconfigInitializer {
    fun getMicroconfig(projectDir: File): Microconfig

    fun findConfigRootDir(projectDir: File): File

    companion object {
        fun create(): MicroconfigInitializer = MicroconfigInitializerImpl()
    }
}
