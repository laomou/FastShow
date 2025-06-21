package mediator

import model.FileSystemModel
import model.FileModel
import presenter.FileListPresenter
import presenter.FolderTreePresenter
import presenter.PathPresenter
import view.MainView
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileSystemView
import kotlin.concurrent.thread

class FastShowMediator(
    private val fileSystemModel: FileSystemModel,
    private val mainView: MainView
) {
    private lateinit var folderTreePresenter: FolderTreePresenter
    private lateinit var fileListPresenter: FileListPresenter
    private lateinit var pathPresenter: PathPresenter

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

        setupSearch()

        onTreeNodeSelected(FileModel(FileSystemView.getFileSystemView().homeDirectory))
    }

    fun onDirectoryChanged(directory: FileModel) {
        currentDirectory = directory
        pathPresenter.setCurrentPath(directory.path)
        fileListPresenter.updateModel(fileSystemModel.getChildren(directory))
        folderTreePresenter.expandToPath(directory)
    }

    fun onTreeNodeSelected(directory: FileModel) {
        currentDirectory = directory
        pathPresenter.setCurrentPath(directory.path)
        fileListPresenter.updateModel(fileSystemModel.getChildren(directory))
    }

    fun getChildren(directory: FileModel): List<FileModel> {
        return fileSystemModel.getChildren(directory)
    }

    private fun setupSearch() {
        val searchView = mainView.getSearchView()

        searchView.setOnSearchAction { query ->
            lastSearchQuery = query
            currentDirectory?.let { dir ->
                thread {
                    val result = if (query.isBlank()) {
                        fileSystemModel.getChildren(dir)
                    } else {
                        fileSystemModel.searchFiles(dir, query)
                    }

                    SwingUtilities.invokeLater {
                        fileListPresenter.updateModel(result)
                    }
                }
            }
        }
    }
}