package presenter

import mediator.FastShowMediator
import model.FileSystemModel
import model.FileModel
import view.components.PathView

class PathPresenter(
    private val view: PathView,
    private val mediator: FastShowMediator
) {
    init {
        view.setPresenter(this)
    }

    fun onPathChanged() {
        val path = view.getCurrentPath()
        val fileMode = FileModel.from(path)

        if (fileMode != null && fileMode.isDirectory) {
            mediator.onDirectoryChanged(fileMode)
        } else {
            view.showError("路径不存在或不是目录")
        }
    }

    fun setCurrentPath(path: String) {
        view.setCurrentPath(path)
    }
}