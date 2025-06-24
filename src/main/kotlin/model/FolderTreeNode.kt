package model

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

class FolderTreeNode(
    val fileEntry: FileEntry,
    val parentNode: FolderTreeNode?,
) : DefaultMutableTreeNode(fileEntry.name) {

    var isLoaded: Boolean = false

    var hasSubFolders: Boolean? = null

    val isDirectory: Boolean get() = fileEntry.isDirectory

    val treePath : TreePath by lazy {
        if (parentNode == null) {
            TreePath(this)
        } else {
            parentNode.treePath.pathByAddingChild(this)
        }
    }

    override fun isLeaf(): Boolean {
        if (!isDirectory) return true
        return hasSubFolders?.not() ?: false
    }
}