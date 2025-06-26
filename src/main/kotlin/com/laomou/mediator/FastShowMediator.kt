package com.laomou.mediator

import com.laomou.model.FileEntry
import com.laomou.model.FileListNode
import com.laomou.model.FileSystemModel
import com.laomou.presenter.*
import com.laomou.view.MainView
import javax.swing.filechooser.FileSystemView

class FastShowMediator(
    private val fileSystemModel: FileSystemModel,
    private val mainView: MainView
) {
    private lateinit var menuBarPresenter: MenuBarPresenter
    private lateinit var toolBarPresenter: ToolBarPresenter
    private lateinit var folderTreePresenter: FolderTreePresenter
    private lateinit var fileListPresenter: FileListPresenter
    private lateinit var pathPresenter: PathPresenter
    private lateinit var searchPresenter: SearchPresenter

    fun initialize() {
        menuBarPresenter = MenuBarPresenter(
            mainView.getMenuBar(),
            this,
        )

        toolBarPresenter = ToolBarPresenter(
            mainView.getToolBar(),
            this
        )

        folderTreePresenter = FolderTreePresenter(
            mainView.getFolderTreeView(),
            fileSystemModel,
            this
        )

        fileListPresenter = FileListPresenter(
            mainView.getFileListView(),
            fileSystemModel,
            this
        )

        pathPresenter = PathPresenter(
            mainView.getPathView(),
            this
        )

        searchPresenter = SearchPresenter(
            mainView.getSearchView(),
            fileSystemModel,
            this
        )

        onTreeNodeSelected(FileEntry(FileSystemView.getFileSystemView().homeDirectory))
    }

    fun showInputDialog(title: String, message: String) : String? {
        return mainView.showInputDialog(title, message)
    }

    fun showErrorMessage(message: String) {
        mainView.showErrorMessage(message)
    }

    fun onDirectoryChanged(directory: FileEntry) {
        pathPresenter.setCurrentPath(directory)
        searchPresenter.setCurrentPath(directory)
        fileListPresenter.loadDirectoryContents(directory)
        folderTreePresenter.expandToPath(directory)
    }

    fun onTreeNodeSelected(directory: FileEntry) {
        pathPresenter.setCurrentPath(directory)
        searchPresenter.setCurrentPath(directory)
        fileListPresenter.loadDirectoryContents(directory)
    }

    fun onEnterSearch() {
        fileListPresenter.enterSearch()
    }

    fun onExitSearch() {
        searchPresenter.exitSearch()
        fileListPresenter.exitSearch()
    }

    fun onSearchOneResult(node: FileListNode) {
        fileListPresenter.appendSearchResult(node)
    }
}