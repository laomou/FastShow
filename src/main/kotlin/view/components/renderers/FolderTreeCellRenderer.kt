package view.components.renderers

import model.FolderTreeNode
import java.awt.Component
import java.io.File
import javax.swing.Icon
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
    private val iconCache = mutableMapOf<File, Icon>()
    private val nameCache = mutableMapOf<File, String>()
    private fun getCachedIcon(file: File): Icon = iconCache.getOrPut(file) { fileSystemView.getSystemIcon(file) }
    private fun getCachedName(file: File): String = nameCache.getOrPut(file) { fileSystemView.getSystemDisplayName(file) }

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
        val file = node.fileEntry.file
        when (val userObj = node.userObject.toString()) {
            "Desktop" -> {
                icon = desktopIcon
                text = "桌面"
            }
            "MyPC" -> {
                icon = computerIcon
                text = "此电脑"
            }
            else -> {
                if (userObj.startsWith("Drive")) {
                    icon = getCachedIcon(file)
                    text = getCachedName(file)
                } else {
                    icon = when {
                        file.isDirectory && expanded -> folderOpenIcon
                        file.isDirectory -> folderIcon
                        else -> fileIcon
                    }
                    text = file.name.toString()
                }
            }
        }

        return this
    }
}