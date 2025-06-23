package presenter

import mediator.FastShowMediator
import model.FileModel
import model.FileSystemModel
import model.FolderTreeNode
import view.components.FolderTreeView
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.filechooser.FileSystemView
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

class FolderTreePresenter(
    private val view: FolderTreeView,
    private val fileSystemMode: FileSystemModel,
    private val mediator: FastShowMediator
) : TreeWillExpandListener {
    private val homeDirectory = FileSystemView.getFileSystemView().homeDirectory
    private lateinit var treeModel: DefaultTreeModel

    init {
        view.setPresenter(this)
        initializeTree()
    }

    private fun initializeTree() {
        val rootNode = FolderTreeNode(FileModel(homeDirectory)).apply {
            userObject = "Desktop"

            val myPCNode = FolderTreeNode(FileModel(homeDirectory)).apply {
                userObject = "MyPC"
                isLoaded = true

                fileSystemMode.getRoots().forEach { drive ->
                    val driveNode = FolderTreeNode(drive).apply {
                        userObject = "Drive"
                    }
                    loadChildren(driveNode)
                    add(driveNode)
                }
            }

            add(myPCNode)
        }

        treeModel = DefaultTreeModel(rootNode)
        view.updateModel(treeModel)
    }

    override fun treeWillExpand(event: TreeExpansionEvent) {
        val path = event.path
        val node = path.lastPathComponent as FolderTreeNode
        if (node.isLoaded) return

        view.setLoadingState(event.path, false)
        loadChildren(node)
        view.setLoadingState(event.path, true)
        treeModel.nodeStructureChanged(node)
    }

    override fun treeWillCollapse(event: TreeExpansionEvent) {}

    fun onTreeNodeSelected() {
        val selectedNode = view.getSelectedNode()
        selectedNode?.let {
            mediator.onTreeNodeSelected(it.fileModel)
        }
    }

    fun expandToPath(directory: FileModel) {
//        findPathTo(directory)?.let(view::expandPath)
    }

    private fun findPathTo(directory: FileModel): TreePath? {
        val pathNodes = generateSequence(directory) { fileSystemMode.getParent(it) }
            .mapNotNull { findNode(it) }
            .toList()
            .asReversed()

        return if (pathNodes.isNotEmpty()) TreePath(pathNodes.toTypedArray()) else null
    }

    private fun findNode(fileModel: FileModel): FolderTreeNode? {
        fun recurse(node: FolderTreeNode): FolderTreeNode? {
            if (node.fileModel.path == fileModel.path) return node
            if (!node.isLoaded && node.isDirectory) loadChildren(node)

            return (0 until node.childCount)
                .asSequence()
                .map { node.getChildAt(it) as FolderTreeNode }
                .mapNotNull(::recurse)
                .firstOrNull()
        }

        return recurse(treeModel.root as FolderTreeNode)
    }

    private fun loadChildren(node: FolderTreeNode) {
        if (node.isLoaded || !node.isDirectory) return

        node.removeAllChildren()

        val children = fileSystemMode.getChildren(node.fileModel)
        children.filter { it.isDirectory }.forEach { child ->
            val childNode = FolderTreeNode(child)
            node.add(childNode)
        }

        node.isLoaded = true
        node.hasSubFolders = children.any { it.isDirectory }
    }
}