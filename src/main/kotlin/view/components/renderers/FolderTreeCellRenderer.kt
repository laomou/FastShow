package view.components.renderers

import model.FolderTreeNode
import java.awt.Component
import javax.swing.JTree
import javax.swing.UIManager
import javax.swing.filechooser.FileSystemView
import javax.swing.tree.DefaultTreeCellRenderer

class FolderTreeCellRenderer : DefaultTreeCellRenderer() {
    private val folderIcon = UIManager.getIcon("Tree.closedIcon")
    private val folderOpenIcon = UIManager.getIcon("Tree.openIcon")
    private val fileIcon = UIManager.getIcon("Tree.leafIcon")
    private val computerIcon = UIManager.getIcon("FileView.computerIcon")
    private val fileSystemView = FileSystemView.getFileSystemView()
    private val desktop = FileSystemView.getFileSystemView().homeDirectory
    private val desktopIcon = fileSystemView.getSystemIcon(desktop)

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
        val file = node.fileModel.file
        when (val userObj = node.userObject) {
            "Desktop" -> {
                icon = desktopIcon
                text = "桌面"
            }
            "MyPC" -> {
                icon = computerIcon
                text = "此电脑"
            }
            "Drive" -> {
                icon = fileSystemView.getSystemIcon(file)
                text = fileSystemView.getSystemDisplayName(file)
            }
            else -> {
                icon = when {
                    file.isDirectory && expanded -> folderOpenIcon
                    file.isDirectory -> folderIcon
                    else -> fileIcon
                }
                text = file.name.toString()
            }
        }

        return this
    }
}