package view.components

import model.FileModel
import presenter.FileListPresenter
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.SwingUtilities

interface FileListView {
    fun setPresenter(presenter: FileListPresenter)
    fun updateModel(model: DefaultListModel<FileModel>)
    fun getSelectedFiles(): List<FileModel>
    fun addMouseListener(l: MouseListener)
}

class FileListViewImpl(private val list: JList<FileModel>) : FileListView {
    private lateinit var presenter: FileListPresenter

    override fun setPresenter(presenter: FileListPresenter) {
        this.presenter = presenter
        list.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && e.button == MouseEvent.BUTTON1) {
                    val index = list.locationToIndex(e.getPoint())
                    if (index >= 0) {
                        val element = list.model.getElementAt(index)
                        if (element.isDirectory) {
                            presenter.navigateToDirectory(list.model.getElementAt(index))
                        }
                    }
                }
            }
        })
    }

    override fun updateModel(model: DefaultListModel<FileModel>) {
        SwingUtilities.invokeLater {
            list.model = model
        }
    }

    override fun getSelectedFiles(): List<FileModel> {
        return list.selectedValuesList
    }

    override fun addMouseListener(l: MouseListener) {
        list.addMouseListener(l)
    }
}