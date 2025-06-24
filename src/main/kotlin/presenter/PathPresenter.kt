package presenter

import mediator.FastShowMediator
import model.FileEntry
import view.components.PathView

class PathPresenter(
    private val view: PathView,
    private val mediator: FastShowMediator
) {
    init {
        view.setPresenter(this)
    }

    fun changeDirectory(directory: FileEntry) {
        mediator.onDirectoryChanged(directory)
    }

    fun setCurrentPath(directory: FileEntry) {
        view.setCurrentPath(directory.path)
    }

    fun showErrorMessage(message: String) {
        mediator.showErrorMessage(message)
    }
}