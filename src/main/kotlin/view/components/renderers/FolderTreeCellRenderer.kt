package view.components.renderers

import model.FolderTreeNode
import java.awt.Component
import javax.swing.JTree
import javax.swing.UIManager
import javax.swing.tree.DefaultTreeCellRenderer

class FolderTreeCellRenderer : DefaultTreeCellRenderer() {
    private val folderIcon = UIManager.getIcon("Tree.closedIcon")
    private val folderOpenIcon = UIManager.getIcon("Tree.openIcon")

    override fun getTreeCellRendererComponent(
        tree: JTree?,
        value: Any?,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component? {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)

        val node = value as FolderTreeNode
        when (val userObj = node.userObject.toString()) {
            "Desktop" -> {
                icon = node.icon
                text = node.name
            }
            "MyPC" -> {
                icon = node.icon
                text = node.name
            }
            else -> {
                if (userObj.startsWith("Drive")) {
                    icon = node.icon
                    text = node.name
                } else {
                    icon = when {
                        expanded -> folderOpenIcon
                        else -> folderIcon
                    }
                    text = node.fileEntry.name
                }
            }
        }

        return this
    }
}