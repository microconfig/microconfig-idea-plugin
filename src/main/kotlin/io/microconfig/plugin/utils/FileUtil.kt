package io.microconfig.plugin.utils

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

fun toVirtualFile(file: File): VirtualFile? = LocalFileSystem.getInstance().findFileByIoFile(file)

fun findDir(root: File, predicate: (File) -> Boolean): File? {
    val queue = ArrayDeque<File>()
    queue.add(root)
    while (queue.isNotEmpty()) {
        val directory = queue.removeFirst()
        if (predicate(directory)) return directory
        directory.listFiles { file -> file.isDirectory && '.' !in file.name }?.let(queue::addAll)
    }
    return null
}
