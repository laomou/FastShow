package presenter

import mediator.FastShowMediator
import model.FileModel
import model.FileSystemModel
import view.components.FileListView
import javax.swing.DefaultListModel
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class FileListPresenter(
    private val view: FileListView,
    private val fileSystemMode: FileSystemModel,
    private val mediator: FastShowMediator
) {
    private val listModel = DefaultListModel<FileModel>()
    private val searchModel = DefaultListModel<FileModel>()

    init {
        view.setPresenter(this)
        view.updateModel(listModel)
    }

    fun enterSearch() {
        searchModel.clear()
        view.updateModel(searchModel)
    }

    fun exitSearch() {
        view.updateModel(listModel)
    }

    fun appendSearchResult(file: FileModel) {
        SwingUtilities.invokeLater {
            searchModel.addElement(file)
        }
    }

    fun setCurrentPath(directory: FileModel) {
        thread {
            val files = fileSystemMode.getChildren(directory)
            SwingUtilities.invokeLater {
                listModel.clear()
                files.forEach { listModel.addElement(it) }
            }
        }
    }

    fun navigateToDirectory(fileModel: FileModel) {
        exitSearch()
        mediator.onDirectoryChanged(fileModel)
    }
}