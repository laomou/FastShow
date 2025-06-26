package com.laomou.presenter

import com.laomou.mediator.FastShowMediator
import com.laomou.model.FileEntry
import com.laomou.view.components.PathView

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
        view.setCurrentPath(directory.absolutePath)
    }

    fun showErrorMessage(message: String) {
        mediator.showErrorMessage(message)
    }
}