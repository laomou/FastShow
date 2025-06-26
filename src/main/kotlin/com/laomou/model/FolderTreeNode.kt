package com.laomou.model

import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

class FolderTreeNode(
    val fileEntry: FileEntry,
    val parentNode: FolderTreeNode?,
) : DefaultMutableTreeNode(fileEntry.name) {

    var isLoaded: Boolean = false

    var hasSubFolders: Boolean? = null

    val treePath : TreePath by lazy {
        if (parentNode == null) {
            TreePath(this)
        } else {
            parentNode.treePath.pathByAddingChild(this)
        }
    }

    var icon: Icon? = null
    var name: String? = null

    override fun isLeaf(): Boolean {
        return hasSubFolders?.not() ?: false
    }
}