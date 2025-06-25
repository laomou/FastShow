package view.components

import model.FileListNode
import presenter.FileListPresenter
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

interface FileListView {
    fun setPresenter(presenter: FileListPresenter)
    fun updateModel(model: DefaultListModel<FileListNode>)
}

class FileListViewImpl(private val list: JList<FileListNode>) : FileListView {
    private lateinit var presenter: FileListPresenter

    override fun setPresenter(presenter: FileListPresenter) {
        this.presenter = presenter
        list.addListSelectionListener {
            presenter.onListSelected(list.selectedValuesList)
        }
        list.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e) ) {
                    if (e.clickCount == 2) {
                        val index = list.locationToIndex(e.getPoint())
                        if (index >= 0) {
                            val element = list.model.getElementAt(index)
                            if (element.isDirectory) {
                                presenter.changeDirectory(element.fileEntry)
                            }
                        }
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.x, e.y)
                }
            }
        })
    }

    override fun updateModel(model: DefaultListModel<FileListNode>) {
        SwingUtilities.invokeLater {
            list.model = model
        }
    }

    private fun showContextMenu(x: Int, y: Int) {
        val popupMenu = JPopupMenu()
        presenter.getContextMenuItems().forEach { item ->
            val menuItem = when {
                item.label.isEmpty() -> JSeparator()
                else -> JMenuItem(item.label).apply {
                    isEnabled = item.enabled
                    addActionListener { item.action() }
                }
            }
            popupMenu.add(menuItem)
        }
        popupMenu.show(list, x, y)
    }

}