package io.microconfig.plugin.microconfig.impl

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import io.microconfig.io.FsReader
import io.microconfig.plugin.utils.toVirtualFile
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Files
import java.util.Optional
import java.util.function.Predicate

class VirtualFileReader : FsReader {
    override fun readFully(file: File): String = documentOf(file)?.text ?: readFromDisk(file)

    override fun readLines(file: File): List<String> = readFully(file).reader().buffered().use { it.readLines() }

    override fun firstLineOf(file: File, predicate: Predicate<String>): Optional<String> =
        Optional.ofNullable(readLines(file).firstOrNull(predicate::test))

    private fun documentOf(file: File): Document? =
        toVirtualFile(file)?.let { FileDocumentManager.getInstance().getDocument(it) }

    private fun readFromDisk(file: File): String =
        try {
            Files.readString(file.toPath())
        } catch (error: IOException) {
            throw UncheckedIOException(error)
        }
}
