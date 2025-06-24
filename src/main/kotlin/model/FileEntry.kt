package model

import java.io.File

data class FileEntry(
    val file: File,
    val isDirectory: Boolean = file.isDirectory,
    val isHidden: Boolean = file.isHidden,
    val isImage: Boolean = isImageFile(file),
    val name: String = file.name,
    val path: String = file.absolutePath
) {
    companion object {
        fun from(file: String): FileEntry? {
            val f = File(file)
            return if (f.exists()) FileEntry(f) else null
        }

        private fun isImageFile(file: File): Boolean {
            val ext = file.name.lowercase()
            return ext.endsWith(".png") || ext.endsWith(".jpg") ||
                    ext.endsWith(".jpeg") || ext.endsWith(".gif")
        }
    }
}