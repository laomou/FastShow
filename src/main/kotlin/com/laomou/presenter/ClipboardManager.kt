package com.laomou.presenter

import com.laomou.model.FileEntry
import com.laomou.model.FileSystemModel

class ClipboardManager {
    sealed class Operation {
        data class Copy(val files: List<FileEntry>) : Operation()
        data class Cut(val files: List<FileEntry>) : Operation()
        object Empty : Operation()
    }

    private var currentOperation: Operation = Operation.Empty

    fun cut(file: FileEntry) = cut(listOf(file))

    fun copy(file: FileEntry) = copy(listOf(file))

    fun cut(files: List<FileEntry>) {
        currentOperation = if (files.isEmpty()) Operation.Empty
        else Operation.Cut(files.distinctBy { it.absolutePath })
    }

    fun copy(files: List<FileEntry>) {
        currentOperation = if (files.isEmpty()) Operation.Empty
        else Operation.Copy(files.distinctBy { it.absolutePath })
    }

    fun paste(targetDir: FileEntry, model: FileSystemModel): Boolean {
        require(targetDir.isDirectory) { "Target must be a directory" }

        return when (val op = currentOperation) {
            is Operation.Copy -> model.copyFiles(op.files, targetDir)
            is Operation.Cut -> model.moveFiles(op.files, targetDir).also {
                if (it) clear()
            }

            Operation.Empty -> false
        }
    }

    fun clear() {
        currentOperation = Operation.Empty
    }

    fun canPaste(): Boolean {
        return currentOperation != Operation.Empty &&
                currentOperation.nodes.isNotEmpty()
    }

    val Operation.nodes: List<FileEntry>
        get() = when (this) {
            is Operation.Copy -> this.files
            is Operation.Cut -> this.nodes
            Operation.Empty -> emptyList()
        }
}