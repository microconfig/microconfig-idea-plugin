package io.microconfig.plugin.microconfig.impl

import io.microconfig.core.Microconfig
import io.microconfig.core.Microconfig.Companion.searchConfigsIn
import io.microconfig.plugin.microconfig.MicroconfigInitializer
import io.microconfig.plugin.utils.findDir
import java.io.File

class MicroconfigInitializerImpl : MicroconfigInitializer {
    override fun getMicroconfig(projectDir: File): Microconfig {
        val configRoot = findConfigRootDir(projectDir)
        // TODO: Make core read templates and Mustache partials through the configured FsReader
        // so unsaved IDEA edits are included.
        val microconfig = searchConfigsIn(configRoot)
            .withDestinationDir(File(projectDir, "build/output"))
            .withFsReader(VirtualFileReader())
        microconfig.environments() // Initialize the lazy environment repository used by component profiles.
        return microconfig
    }

    override fun findConfigRootDir(projectDir: File): File =
        findDir(projectDir, ::containsMicroconfigDirectories)
            ?: error("Can't find 'components' and 'envs' folders on the same level")

    private fun containsMicroconfigDirectories(directory: File): Boolean =
        File(directory, "envs").isDirectory && File(directory, "components").isDirectory
}
