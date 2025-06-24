package view.components

import model.FolderTreeNode
import presenter.FolderTreePresenter
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

interface FolderTreeView {
    fun setPresenter(presenter: FolderTreePresenter)
    fun updateModel(model: DefaultTreeModel)
    fun expandToPath(path: TreePath)
}

class FolderTreeViewImpl(private val tree: JTree) : FolderTreeView {
    private lateinit var presenter: FolderTreePresenter

    override fun setPresenter(presenter: FolderTreePresenter) {
        this.presenter = presenter
        tree.addTreeWillExpandListener(this.presenter)
        tree.addTreeSelectionListener {
            val selectedNode = tree.lastSelectedPathComponent
            if (selectedNode is FolderTreeNode) {
                presenter.onTreeNodeSelected(selectedNode)
            }
        }
        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val path = tree.getPathForLocation(e.x, e.y)
                    path?.let {
                        tree.selectionPath = path
                        showContextMenu(e.x, e.y)
                    }
                }
            }
        })
    }

    override fun updateModel(model: DefaultTreeModel) {
        SwingUtilities.invokeLater { tree.model = model }
    }

    override fun expandToPath(path: TreePath) {
        SwingUtilities.invokeLater {
            tree.expandPath(path)
            tree.selectionPath = path
            tree.scrollPathToVisible(path)
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
        popupMenu.show(tree, x, y)
    }
}