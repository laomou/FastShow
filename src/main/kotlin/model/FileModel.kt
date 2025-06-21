package model

import java.io.File

data class FileModel(
    val file: File,
    val isDirectory: Boolean = file.isDirectory,
    val isHidden: Boolean = file.isHidden,
    val name: String = file.name,
    val path: String = file.absolutePath
) {
    companion object {
        fun from(file: String): FileModel? {
            val f = File(file)
            return if (f.exists()) FileModel(f) else null
        }
    }
}