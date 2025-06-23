package presenter

import mediator.FastShowMediator
import model.FileModel
import view.components.PathView

class PathPresenter(
    private val view: PathView,
    private val mediator: FastShowMediator
) {
    init {
        view.setPresenter(this)
    }

    fun onPathChanged(directory: FileModel?) {
        if (directory != null && directory.isDirectory) {
            mediator.onDirectoryChanged(directory)
        } else {
            view.showError("路径不存在或不是目录")
        }
    }

    fun setCurrentPath(path: String) {
        view.setCurrentPath(path)
    }
}