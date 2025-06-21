package presenter

import mediator.FastShowMediator
import model.FileSystemModel
import model.FileModel
import view.components.FileListView

class FileListPresenter(
    private val view: FileListView,
    private val fileSystemMode: FileSystemModel,
    private val mediator: FastShowMediator
) {
    init {
        view.setPresenter(this)
    }

    fun updateModel(files: List<FileModel>) {
        view.updateModel(files)
    }

    fun onDirectoryChanged(fileModel: FileModel) {
        mediator.onDirectoryChanged(fileModel)
    }
}