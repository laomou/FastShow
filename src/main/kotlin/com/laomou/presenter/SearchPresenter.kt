package com.laomou.presenter

import com.laomou.mediator.FastShowMediator
import com.laomou.model.FileEntry
import com.laomou.model.FileListNode
import com.laomou.model.FileSystemModel
import com.laomou.view.components.SearchView
import kotlin.concurrent.thread

class SearchPresenter (
    private val view: SearchView,
    private val fileSystemModel: FileSystemModel,
    private val mediator: FastShowMediator
) {
    private var lastSearchQuery: String = ""
    private var currentDirectory: FileEntry? = null
    init {
        view.setPresenter(this)
        view.setOnSearchAction { query ->
            lastSearchQuery = query

            if (query.isBlank()) {
                mediator.onExitSearch()
                return@setOnSearchAction
            }

            mediator.onEnterSearch()

            currentDirectory?.let { dir ->
                thread {
                    val allFiles = fileSystemModel.getChildren(dir).map { FileListNode(it) }.filter { it.isImage || it.isDirectory }
                    val total = allFiles.size

                    view.setProgress(0)
                    allFiles.forEachIndexed { index, node ->
                        if (node.fileEntry.name.contains(query, ignoreCase = true)) {
                            mediator.onSearchOneResult(node)
                        }

                        val progress = ((index + 1) * 100) / total
                        view.setProgress(progress)
                    }
                }
            }
        }
    }

    fun exitSearch() {
        view.clearSearch()
    }

    fun setCurrentPath(path: FileEntry) {
        view.clearSearch()
        currentDirectory = path
    }
}