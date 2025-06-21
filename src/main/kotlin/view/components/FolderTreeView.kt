package view.components

import model.FolderTreeNode
import presenter.FolderTreePresenter
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

interface FolderTreeView {
    fun setPresenter(presenter: FolderTreePresenter)
    fun updateModel(model: DefaultTreeModel)
    fun expandPath(path: TreePath)
    fun getSelectedNode(): FolderTreeNode?
    fun setLoadingState(path: TreePath, loading: Boolean)
    fun refresh()
}

class FolderTreeViewImpl(private val tree: JTree) : FolderTreeView {
    private lateinit var presenter: FolderTreePresenter

    override fun setPresenter(presenter: FolderTreePresenter) {
        this.presenter = presenter
        tree.addTreeWillExpandListener(this.presenter)
        tree.addTreeSelectionListener { presenter.onTreeNodeSelected() }
        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                }
            }
        })
    }

    override fun updateModel(model: DefaultTreeModel) {
        SwingUtilities.invokeLater { tree.model = model }
    }

    override fun expandPath(path: TreePath) {
        SwingUtilities.invokeLater {
            tree.expandPath(path)
            tree.scrollPathToVisible(path)
        }
    }

    override fun getSelectedNode(): FolderTreeNode? {
        return tree.lastSelectedPathComponent as FolderTreeNode?
    }

    override fun setLoadingState(path: TreePath, loading: Boolean) {
        SwingUtilities.invokeLater {
            val node = path.lastPathComponent as? FolderTreeNode
            node?.let {
                tree.treeDidChange()
            }
        }
    }

    override fun refresh() {
        SwingUtilities.invokeLater {
            (tree.model as? DefaultTreeModel)?.reload()
        }
    }
}