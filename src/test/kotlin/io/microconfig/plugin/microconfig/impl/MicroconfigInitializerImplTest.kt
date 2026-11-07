package io.microconfig.plugin.microconfig.impl

import com.intellij.openapi.util.io.FileUtil
import io.microconfig.plugin.MicroconfigRepoTestCase
import io.microconfig.plugin.microconfig.MicroconfigInitializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.File

class MicroconfigInitializerImplTest : MicroconfigRepoTestCase() {
    private val initializer = MicroconfigInitializer.create()

    @Test
    fun findsRepositoryWhenProjectIsTheConfigRoot() {
        assertEquals(repoDir, initializer.findConfigRootDir(repoDir))
    }

    @Test
    fun findsRepositoryInsideProject() {
        assertEquals(repoDir, initializer.findConfigRootDir(projectDir))
    }

    @Test
    fun choosesNearestRepositoryBeforeDeeperMatches() {
        File(repoDir, "nested/components").mkdirs()
        File(repoDir, "nested/envs").mkdirs()

        assertEquals(repoDir, initializer.findConfigRootDir(projectDir))
    }

    @Test
    fun rejectsComponentsAndEnvironmentsOnDifferentLevels() {
        FileUtil.delete(repoDir)
        File(repoDir, "components").mkdirs()
        File(repoDir, "nested/envs").mkdirs()

        val error = assertThrows(IllegalStateException::class.java) { initializer.findConfigRootDir(projectDir) }

        assertEquals("Can't find 'components' and 'envs' folders on the same level", error.message)
    }

    @Test
    fun ignoresHiddenRepositoriesAndFilesNamedAsDirectories() {
        FileUtil.delete(repoDir)
        File(projectDir, ".hidden/components").mkdirs()
        File(projectDir, ".hidden/envs").mkdirs()
        repoDir.mkdirs()
        File(repoDir, "components").writeText("")
        File(repoDir, "envs").writeText("")

        assertThrows(IllegalStateException::class.java) { initializer.findConfigRootDir(projectDir) }
    }

    @Test
    fun configuresCoreRootDestinationAndVirtualFileReader() {
        val microconfig = initializer.getMicroconfig(projectDir)

        assertEquals(repoDir.canonicalFile, microconfig.rootDir())
        assertEquals(File(projectDir, "build/output"), microconfig.destinationDir())
        assertInstanceOf(VirtualFileReader::class.java, microconfig.fsReader())
        assertEquals(setOf("dev", "prod"), microconfig.environments().environmentNames())
    }
}
