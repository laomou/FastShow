package presenter

import mediator.FastShowMediator
import model.FileModel
import model.FileSystemModel
import view.components.SearchView
import kotlin.concurrent.thread

class SearchPresenter (
    private val view: SearchView,
    private val fileSystemModel: FileSystemModel,
    private val mediator: FastShowMediator
) {
    private var lastSearchQuery: String = ""
    private var currentDirectory: FileModel? = null
    init {
        view.setPresenter(this)
        view.setOnSearchAction { query ->
            lastSearchQuery = query

            if (query.isBlank()) {
                view.clearSearch()
                mediator.onExitSearch()
                return@setOnSearchAction
            } else {
                mediator.onEnterSearch()
            }

            currentDirectory?.let { dir ->
                thread {
                    val allFiles = fileSystemModel.getChildren(dir)
                    val total = allFiles.size

                    view.setProgress(0)
                    allFiles.forEachIndexed { index, file ->
                        if (file.name.contains(query, ignoreCase = true)) {
                            mediator.onSearchOneResult(file)
                        }

                        val progress = ((index + 1) * 100) / total
                        view.setProgress(progress)
                    }
                }
            }
        }
    }

    fun setCurrentPath(path: FileModel?) {
        view.clearSearch()
        currentDirectory = path
    }
}