package com.laomou.presenter

import com.laomou.mediator.FastShowMediator
import com.laomou.model.FileEntry
import com.laomou.model.FileSystemModel
import com.laomou.model.FolderTreeNode
import com.laomou.model.MenuItem
import com.laomou.view.components.FolderTreeView
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.filechooser.FileSystemView
import javax.swing.tree.DefaultTreeModel

class FolderTreePresenter(
    private val view: FolderTreeView,
    private val fileSystemMode: FileSystemModel,
    private val mediator: FastShowMediator
) : TreeWillExpandListener {
    private val fileSystemView = FileSystemView.getFileSystemView()
    private val homeDirectory = fileSystemView.homeDirectory
    private val clipboard: ClipboardManager = ClipboardManager()
    private val treePathCache = mutableMapOf<FileEntry, FolderTreeNode>()
    private lateinit var treeModel: DefaultTreeModel
    private lateinit var rootTreeNode: FolderTreeNode
    private lateinit var selectedTreeNode: FolderTreeNode

    init {
        view.setPresenter(this)
        initializeTree()
    }

    private fun initializeTree() {
        rootTreeNode = FolderTreeNode(FileEntry(homeDirectory), null).apply {
            userObject = "Desktop"
            isLoaded = true
            icon = fileSystemView.getSystemIcon(homeDirectory)
            name = "桌面"

            val myPCNode = FolderTreeNode(FileEntry(homeDirectory), this).apply {
                userObject = "MyPC"
                isLoaded = true
                icon = fileSystemView.getSystemIcon(homeDirectory)
                name = "此电脑"

                fileSystemMode.getRoots().forEachIndexed { idx, drive ->
                    val driveNode = FolderTreeNode(drive, this).apply {
                        userObject = "Drive_${idx}"
                        icon = fileSystemView.getSystemIcon(drive.file)
                        name = fileSystemView.getSystemDisplayName(drive.file)
                    }
                    loadChildren(driveNode)
                    add(driveNode)

                    treePathCache[drive] = driveNode
                }
            }

            add(myPCNode)
        }

        selectedTreeNode = rootTreeNode
        treeModel = DefaultTreeModel(rootTreeNode)
        view.updateModel(treeModel)
    }

    override fun treeWillExpand(event: TreeExpansionEvent) {
        val path = event.path
        val node = path.lastPathComponent as FolderTreeNode
        if (node.isLoaded) return

        loadChildren(node)
        treeModel.nodeStructureChanged(node)
    }

    override fun treeWillCollapse(event: TreeExpansionEvent) {}

    fun getContextMenuItems(): List<MenuItem> {
        return listOf(
            MenuItem("刷新", { refreshChildren(selectedTreeNode) }),
            MenuItem("", { }),
            MenuItem("剪切", { clipboard.cut(selectedTreeNode.fileEntry) }),
            MenuItem("拷贝", { clipboard.copy(selectedTreeNode.fileEntry) }),
            MenuItem(
                "粘贴",
                { clipboard.paste(selectedTreeNode.fileEntry, fileSystemMode); refreshChildren(selectedTreeNode) },
                enabled = clipboard.canPaste()
            ),
            MenuItem("", { }),
            MenuItem("新建文件夹", {
                selectedTreeNode.let {
                    val folderName = mediator.showInputDialog("新建文件夹", "新建文件夹名称")
                    if (!folderName.isNullOrBlank()) {
                        fileSystemMode.createNewFolder(it.fileEntry, folderName)
                    }
                }
                refreshChildren(selectedTreeNode)
            }),
            MenuItem("删除", {
                fileSystemMode.deleteFiles(arrayListOf(selectedTreeNode.fileEntry))
                selectedTreeNode.parentNode?.let {
                    refreshChildren(it)
                }
            }),
            MenuItem("重命名", {
                selectedTreeNode.let {
                    val newName = mediator.showInputDialog("重命名", "文件名： ${it.fileEntry.name}\n重命名为：")
                    if (!newName.isNullOrBlank()) {
                        fileSystemMode.renameFile(it.fileEntry, newName)
                    }
                }
                refreshChildren(selectedTreeNode)
            }),
        )
    }

    fun onTreeNodeSelected(treeNode: FolderTreeNode) {
        selectedTreeNode = treeNode
        mediator.onTreeNodeSelected(treeNode.fileEntry)
    }

    fun expandToPath(directory: FileEntry) {
        treePathCache[directory]?.let { view.expandToPath(it.treePath) }
    }

    fun refreshChildren(node: FolderTreeNode) {
        treePathCache[node.fileEntry]?.let {
            node.isLoaded = false
            loadChildren(it)
            treeModel.nodeStructureChanged(node)
        }
    }

    private fun loadChildren(node: FolderTreeNode) {
        if (node.isLoaded) return

        node.removeAllChildren()

        val children = fileSystemMode.getChildren(node.fileEntry)
        children.filter { it.isDirectory }.forEach { child ->
            val childNode = FolderTreeNode(child, node)
            node.add(childNode)

            treePathCache[childNode.fileEntry] = childNode

            val grandChildren = fileSystemMode.getChildren(childNode.fileEntry)
            childNode.hasSubFolders = grandChildren.any { it.isDirectory }
        }

        node.isLoaded = true
        node.hasSubFolders = children.any { it.isDirectory }
    }
}