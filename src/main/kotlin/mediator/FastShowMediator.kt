package mediator

import model.FileModel
import model.FileSystemModel
import presenter.FileListPresenter
import presenter.FolderTreePresenter
import presenter.PathPresenter
import presenter.SearchPresenter
import view.MainView
import javax.swing.filechooser.FileSystemView

class FastShowMediator(
    private val fileSystemModel: FileSystemModel,
    private val mainView: MainView
) {
    private lateinit var folderTreePresenter: FolderTreePresenter
    private lateinit var fileListPresenter: FileListPresenter
    private lateinit var pathPresenter: PathPresenter
    private lateinit var searchPresenter: SearchPresenter

    private var currentDirectory: FileModel? = null
    private var lastSearchQuery: String = ""

    fun initialize() {
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

        onTreeNodeSelected(FileModel(FileSystemView.getFileSystemView().homeDirectory))
    }

    fun onDirectoryChanged(directory: FileModel) {
        currentDirectory = directory
        pathPresenter.setCurrentPath(directory.path)
        searchPresenter.setCurrentPath(directory)
        fileListPresenter.setCurrentPath(directory)
        folderTreePresenter.expandToPath(directory)
    }

    fun onTreeNodeSelected(directory: FileModel) {
        currentDirectory = directory
        pathPresenter.setCurrentPath(directory.path)
        searchPresenter.setCurrentPath(directory)
        fileListPresenter.setCurrentPath(directory)
    }

    fun onEnterSearch() {
        fileListPresenter.enterSearch()
    }

    fun onExitSearch() {
        fileListPresenter.exitSearch()
    }

    fun onSearchOneResult(fileModel: FileModel) {
        fileListPresenter.appendSearchResult(fileModel)
    }

}