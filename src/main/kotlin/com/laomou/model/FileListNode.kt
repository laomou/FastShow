package com.laomou.model

import java.io.File
import javax.swing.Icon

class FileListNode(
    val fileEntry: FileEntry,
) {
    val isDirectory = fileEntry.isDirectory
    val isImage: Boolean = isImageFile(fileEntry.file)
    var icon: Icon? = null
    var name: String? = fileEntry.name

    private fun isImageFile(file: File): Boolean {
        val ext = file.name.lowercase()
        return ext.endsWith(".png") || ext.endsWith(".jpg") ||
                ext.endsWith(".jpeg") || ext.endsWith(".gif")
    }
}