package model

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

interface FileSystemModel {
    fun getRoots(): List<FileEntry>
    fun getChildren(parent: FileEntry): List<FileEntry>
    fun getAllChildren(directory: FileEntry): List<FileEntry>
    fun getParent(child: FileEntry): FileEntry?
    fun moveFiles(sources: List<FileEntry>, destination: FileEntry): Boolean
    fun copyFiles(sources: List<FileEntry>, destination: FileEntry): Boolean
    fun deleteFiles(files: List<FileEntry>): Boolean
    fun createNewFolder(parent: FileEntry, name: String): FileEntry?
    fun renameFile(file: FileEntry, newName: String): Boolean
}

class DefaultFileSystemModel : FileSystemModel {
    override fun getRoots(): List<FileEntry> {
        return File.listRoots().map { FileEntry(it) }
    }

    override fun getChildren(parent: FileEntry): List<FileEntry> {
        return parent.file.listVisibleChildren()
    }

    override fun getAllChildren(directory: FileEntry): List<FileEntry> {
        return directory.file.searchRecursively()
    }

    override fun getParent(child: FileEntry): FileEntry? {
        return child.file.parentFile?.let { FileEntry(it) }
    }

    override fun moveFiles(sources: List<FileEntry>, destination: FileEntry): Boolean {
        return try {
            sources.all { node ->
                Files.move(
                    Path(node.path),
                    Path(destination.path, node.name),
                    StandardCopyOption.REPLACE_EXISTING
                )
                true
            }
        } catch (e: IOException) {
            false
        }
    }

    override fun copyFiles(sources: List<FileEntry>, destination: FileEntry): Boolean {
        return try {
            sources.all { node ->
                val originalName = node.name
                val dotIndex = originalName.lastIndexOf('.')
                val newName = if (dotIndex != -1) {
                    val namePart = originalName.substring(0, dotIndex)
                    val extPart = originalName.substring(dotIndex)
                    "$namePart - 副本$extPart"
                } else {
                    "$originalName - 副本"
                }
                Files.copy(
                    Path(node.path),
                    Path(destination.path, newName),
                    StandardCopyOption.REPLACE_EXISTING
                )
                true
            }
        } catch (e: IOException) {
            false
        }
    }

    override fun deleteFiles(files: List<FileEntry>): Boolean {
        return try {
            files.all { node ->
                Files.deleteIfExists(Path(node.path))
                true
            }
        } catch (e: IOException) {
            false
        }
    }

    override fun createNewFolder(parent: FileEntry, name: String): FileEntry? {
        require(parent.isDirectory)
        val newFolder = File(parent.file, name)
        return if (newFolder.mkdir()) FileEntry(newFolder) else null
    }

    override fun renameFile(file: FileEntry, newName: String): Boolean {
        val originalName = file.name
        val finalName = if (!file.isDirectory) {
            val dotIndex = originalName.lastIndexOf('.')
            if (!newName.contains('.') && dotIndex != -1) {
                val extension = originalName.substring(dotIndex)
                "$newName$extension"
            } else {
                newName
            }
        } else {
            newName
        }

        val newFile = File(file.file.parentFile, finalName)
        return file.file.renameTo(newFile)
    }

    private fun File.listVisibleChildren(): List<FileEntry> {
        return listFiles()
            ?.filter { !it.isHidden }
            ?.map { FileEntry(it) }
            ?: emptyList()
    }

    private fun File.searchRecursively(): List<FileEntry> {
        return walkTopDown()
            .filter { !it.isHidden }
            .map { FileEntry(it) }
            .toList()
    }
}