package model

import javax.swing.tree.DefaultMutableTreeNode

class FolderTreeNode(
    val fileModel: FileModel,
) : DefaultMutableTreeNode() {

    var isLoaded: Boolean = false

    var hasSubFolders: Boolean? = null

    val isDirectory: Boolean get() = fileModel.isDirectory

    override fun isLeaf(): Boolean {
        if (!isDirectory) return true
        return hasSubFolders?.not() ?: false
    }
}