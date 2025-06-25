package presenter

import mediator.FastShowMediator
import model.FileEntry
import model.FileListNode
import model.FileSystemModel
import model.MenuItem
import view.components.FileListView
import javax.swing.DefaultListModel
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

class FileListPresenter(
    private val view: FileListView,
    private val fileSystemMode: FileSystemModel,
    private val mediator: FastShowMediator
) {
    private var currentDirectory: FileEntry? = null
    private var selectedNodeList: List<FileListNode> = arrayListOf()
    private val listModel = DefaultListModel<FileListNode>()
    private val searchModel = DefaultListModel<FileListNode>()
    private val clipboard: ClipboardManager = ClipboardManager()


    init {
        view.setPresenter(this)
        view.updateModel(listModel)
    }

    fun onListSelected(list: List<FileListNode>) {
        selectedNodeList = list
    }

    fun enterSearch() {
        searchModel.clear()
        view.updateModel(searchModel)
    }

    fun appendSearchResult(node: FileListNode) {
        SwingUtilities.invokeLater {
            searchModel.addElement(node)
        }
    }

    fun exitSearch() {
        view.updateModel(listModel)
    }

    fun loadDirectoryContents(directory: FileEntry) {
        currentDirectory = directory
        thread {
            val files = fileSystemMode.getChildren(directory).map { FileListNode(it) }.filter { it.isImage || it.isDirectory }
            SwingUtilities.invokeLater {
                listModel.clear()
                files.forEach { listModel.addElement(it) }
            }
        }
    }

    fun changeDirectory(directory: FileEntry) {
        exitSearch()
        currentDirectory = directory
        mediator.onDirectoryChanged(directory)
    }

    fun getContextMenuItems(): List<MenuItem> {
        return listOf(
            MenuItem("刷新", { refreshCurrentDirectory() }),
            MenuItem("", { }),
            MenuItem("剪切", { clipboard.cut(selectedNodeList.map { it.fileEntry }) }),
            MenuItem("拷贝", { clipboard.copy(selectedNodeList.map { it.fileEntry }) }),
            MenuItem(
                "粘贴",
                { currentDirectory?.let { clipboard.paste(it, fileSystemMode); refreshCurrentDirectory() } },
                enabled = clipboard.canPaste()
            ),
            MenuItem("", { }),
            MenuItem("新建文件夹", {
                currentDirectory?.let {
                    val folderName = mediator.showInputDialog("新建文件夹", "新建文件夹名称")
                    if (!folderName.isNullOrBlank()) {
                        fileSystemMode.createNewFolder(it, folderName)
                    }
                }
                refreshCurrentDirectory()
            }),
            MenuItem(
                "删除",
                { fileSystemMode.deleteFiles(selectedNodeList.map { it.fileEntry }); refreshCurrentDirectory() },
                selectedNodeList.isNotEmpty()
            ),
            MenuItem("重命名", {
                selectedNodeList.forEach { it ->
                    val newName = mediator.showInputDialog("重命名", "文件名： ${it.name}\n重命名为：")
                    if (!newName.isNullOrBlank()) {
                        fileSystemMode.renameFile(it.fileEntry, newName)
                    }
                }
                refreshCurrentDirectory()
            }),
        )
    }

    private fun refreshCurrentDirectory() {
        currentDirectory?.let {
            loadDirectoryContents(it)
        }
    }
}