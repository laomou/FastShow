package com.laomou.view.components

import com.laomou.model.FileEntry
import com.laomou.presenter.PathPresenter
import javax.swing.JTextField
import javax.swing.SwingUtilities

interface PathView {
    fun setPresenter(presenter: PathPresenter)
    fun setCurrentPath(path: String)
}

class PathViewImpl(private val textField: JTextField) : PathView {
    private lateinit var presenter: PathPresenter

    override fun setPresenter(presenter: PathPresenter) {
        this.presenter = presenter
        textField.addActionListener {
            val directory = FileEntry.from(textField.text)
            if (directory != null && directory.isDirectory) {
                presenter.changeDirectory(directory)
            } else {
                presenter.showErrorMessage("路径不存在或不是目录")
            }
        }
    }

    override fun setCurrentPath(path: String) {
        SwingUtilities.invokeLater {
            textField.text = path
        }
    }
}