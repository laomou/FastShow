package model

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

interface FileSystemModel {
    fun getRoots(): List<FileModel>
    fun getChildren(parent: FileModel): List<FileModel>
    fun getParent(child: FileModel): FileModel?
    fun searchFiles(directory: FileModel, query: String): List<FileModel>
    fun copyFile(source: FileModel, destination: FileModel): Boolean
    fun deleteFile(file: FileModel): Boolean
    fun createNewFolder(parent: FileModel, name: String): FileModel?
    fun renameFile(file: FileModel, newName: String): Boolean
}

class DefaultFileSystemModel : FileSystemModel {
    override fun getRoots(): List<FileModel> {
        return File.listRoots().map { FileModel(it) }
    }

    override fun getChildren(parent: FileModel): List<FileModel> {
        return parent.file.listVisibleChildren()
    }

    override fun getParent(child: FileModel): FileModel? {
        return child.file.parentFile?.let { FileModel(it) }
    }

    override fun searchFiles(directory: FileModel, query: String): List<FileModel> {
        return directory.file.listVisibleChildren().filter { it.name.contains(query, ignoreCase = true) }
    }

    override fun copyFile(source: FileModel, destination: FileModel): Boolean {
        return try {
            Files.copy(
                source.file.toPath(),
                destination.file.toPath().resolve(source.file.name),
                StandardCopyOption.REPLACE_EXISTING
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun deleteFile(file: FileModel): Boolean {
        return if (file.isDirectory) {
            file.file.deleteRecursively()
        } else {
            file.file.delete()
        }
    }

    override fun createNewFolder(parent: FileModel, name: String): FileModel? {
        val newFolder = File(parent.file, name)
        return if (newFolder.mkdir()) FileModel(newFolder) else null
    }

    override fun renameFile(file: FileModel, newName: String): Boolean {
        val newFile = File(file.file.parentFile, newName)
        return file.file.renameTo(newFile)
    }

    private fun File.listVisibleChildren(): List<FileModel> {
        return listFiles()
            ?.filter { !it.isHidden }
            ?.map { FileModel(it) }
            ?: emptyList()
    }

    private fun File.searchRecursively(query: String): List<FileModel> {
        return walkTopDown()
            .filter { it.isFile && it.name.contains(query, ignoreCase = true) }
            .map { FileModel(it) }
            .toList()
    }
}