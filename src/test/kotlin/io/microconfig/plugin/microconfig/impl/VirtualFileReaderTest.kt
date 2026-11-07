package io.microconfig.plugin.microconfig.impl

import com.intellij.openapi.command.WriteCommandAction
import io.microconfig.plugin.MicroconfigRepoTestCase
import io.microconfig.plugin.utils.toVirtualFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.Optional
import java.io.UncheckedIOException
import org.junit.jupiter.api.Assertions.assertThrows

class VirtualFileReaderTest : MicroconfigRepoTestCase() {
    @Test
    fun readsFileUnknownToVfsFromDisk() {
        val file = repoFile("components/eureka/application.dev.yaml")
        checkNotNull(toVirtualFile(file.parentFile)).children
        Files.writeString(file.toPath(), "server:\n  port: 7761\n")
        assertNull(toVirtualFile(file))
        assertEquals("server:\n  port: 7761\n", VirtualFileReader().readFully(file))
    }

    @Test
    fun readsUnsavedLinesAndFirstMatchingLine() {
        openWithCaretAfter("components/eureka/application.yaml", "")
        WriteCommandAction.runWriteCommandAction(project()) {
            fixture.editor.document.setText("first\nsecond\nthird\n")
        }
        val reader = VirtualFileReader()
        val file = repoFile("components/eureka/application.yaml")
        assertEquals(listOf("first", "second", "third"), reader.readLines(file))
        assertEquals(Optional.of("second"), reader.firstLineOf(file) { it.startsWith("sec") })
    }

    @Test
    fun handlesCrLfAndEmptyLinesForFilesOutsideVfs() {
        val file = repoFile("components/eureka/reader.txt")
        checkNotNull(toVirtualFile(file.parentFile)).children
        Files.writeString(file.toPath(), "first\r\n\r\nthird\r\n")
        assertNull(toVirtualFile(file))
        val reader = VirtualFileReader()

        assertEquals("first\r\n\r\nthird\r\n", reader.readFully(file))
        assertEquals(listOf("first", "", "third"), reader.readLines(file))
    }

    @Test
    fun returnsFirstMatchingLineAndEmptyWhenNothingMatches() {
        val file = writeRepoFile("reader.txt", "one\ntwo\nthree\n")
        val reader = VirtualFileReader()

        assertEquals(Optional.of("two"), reader.firstLineOf(file) { it.startsWith("t") })
        assertEquals(Optional.empty<String>(), reader.firstLineOf(file) { it.startsWith("missing") })
    }

    @Test
    fun readsEmptyFile() {
        val file = writeRepoFile("reader.txt", "")
        val reader = VirtualFileReader()

        assertEquals("", reader.readFully(file))
        assertEquals(emptyList<String>(), reader.readLines(file))
        assertEquals(Optional.empty<String>(), reader.firstLineOf(file) { true })
    }

    @Test
    fun reportsUnreadableFile() {
        assertThrows(UncheckedIOException::class.java) { VirtualFileReader().readFully(repoFile("missing.txt")) }
    }
}
