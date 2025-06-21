package view.components

import model.FileModel
import presenter.FileListPresenter
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JList
import javax.swing.SwingUtilities

interface FileListView {
    fun setPresenter(presenter: FileListPresenter)
    fun updateModel(files: List<FileModel>)
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
                        presenter.onDirectoryChanged(list.model.getElementAt(index))
                    }
                }
            }
        })
    }

    override fun updateModel(files: List<FileModel>) {
        SwingUtilities.invokeLater {
            list.setListData(files.toTypedArray())
            list.ensureIndexIsVisible(0)
        }
    }

    override fun getSelectedFiles(): List<FileModel> {
        return list.selectedValuesList
    }

    override fun addMouseListener(l: MouseListener) {
        list.addMouseListener(l)
    }
}