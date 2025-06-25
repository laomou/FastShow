package model

import java.io.File

data class FileEntry(
    val file: File,
    val isDirectory: Boolean = file.isDirectory,
    val name: String = file.name,
    val absolutePath: String = file.absolutePath
) {
    companion object {
        fun from(file: String): FileEntry? {
            val f = File(file)
            return if (f.exists()) FileEntry(f) else null
        }
    }
}