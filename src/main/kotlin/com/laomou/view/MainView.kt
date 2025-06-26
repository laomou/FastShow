package com.laomou.view

import com.laomou.model.FileListNode
import com.laomou.view.components.*
import com.laomou.view.components.renderers.FileListRenderer
import com.laomou.view.components.renderers.FolderTreeCellRenderer
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*
import javax.swing.tree.DefaultTreeModel


interface MainView {
    fun initializeUI()
    fun getMenuBar(): MenuBar
    fun getToolBar(): ToolBar
    fun getFolderTreeView(): FolderTreeView
    fun getFileListView(): FileListView
    fun getPathView(): PathView
    fun getSearchView(): SearchView
    fun getFrame(): JFrame
    fun showErrorMessage(message: String)
    fun showInputDialog(title: String, message: String): String?
}

class MainViewImpl : MainView {
    private val frame = JFrame("FastShow Image Viewer")
    private val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
    private lateinit var menuBar: MenuBar
    private lateinit var toolBar: ToolBar
    private lateinit var fileTreeView: FolderTreeView
    private lateinit var fileListView: FileListView
    private lateinit var pathView: PathView
    private lateinit var searchView: SearchView

    override fun initializeUI() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.extendedState = JFrame.MAXIMIZED_BOTH
        frame.setSize(1200, 800)

        val jMenuBar = JMenuBar()
        frame.jMenuBar = jMenuBar
        menuBar = MenuBarImpl(jMenuBar)

        val jToolBar = JToolBar().apply {
            isFloatable = false
        }
        toolBar = ToolBarImpl(jToolBar)

        val model = DefaultTreeModel(null)
        val tree = JTree(model).apply {
            showsRootHandles = true
            isRootVisible = true
            cellRenderer = FolderTreeCellRenderer()
        }
        fileTreeView = FolderTreeViewImpl(tree)

        val leftPanel = JScrollPane(tree)

        val pathField = JTextField().apply {
            toolTipText = "输入路径后按回车跳转"
        }
        pathView = PathViewImpl(pathField)

        val searchField = SearchField().apply {
            toolTipText = "搜索"
            columns = 30
            isOpaque = false
            background = Color.WHITE
        }
        searchView = SearchViewImpl(searchField)

        val fileList = JList<FileListNode>().apply {
            selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            layoutOrientation = JList.HORIZONTAL_WRAP
            fixedCellWidth = 140
            fixedCellHeight = 170
            cellRenderer = FileListRenderer()
            visibleRowCount = -1
        }
        fileListView = FileListViewImpl(fileList)

        val rightPanel = JPanel(BorderLayout()).apply {
            val topPanel = JPanel(BorderLayout(5, 5)).apply {
                border = BorderFactory.createEmptyBorder(0, 8, 0, 8)
                val pathPanel = JPanel(BorderLayout()).apply {
                    add(JLabel("路径:"), BorderLayout.WEST)
                    add(pathField, BorderLayout.CENTER)
                }
                val searchPanel = JPanel(BorderLayout()).apply {
                    add(JLabel("搜索:"), BorderLayout.WEST)
                    add(searchField, BorderLayout.CENTER)
                }
                add(pathPanel, BorderLayout.CENTER)
                add(searchPanel, BorderLayout.EAST)
            }
            add(topPanel, BorderLayout.NORTH)
            add(JScrollPane(fileList).apply {
                border = BorderFactory.createEmptyBorder()
                horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            }, BorderLayout.CENTER)
        }

        splitPane.apply {
            leftComponent = leftPanel
            rightComponent = rightPanel
            dividerLocation = 250
            border = BorderFactory.createEmptyBorder()
        }

        frame.contentPane.apply {
            layout = BorderLayout()
            add(jToolBar, BorderLayout.NORTH)
            add(splitPane, BorderLayout.CENTER)
        }

        frame.isVisible = true
    }

    override fun getMenuBar(): MenuBar = menuBar
    override fun getToolBar(): ToolBar = toolBar
    override fun getFolderTreeView(): FolderTreeView = fileTreeView
    override fun getFileListView(): FileListView = fileListView
    override fun getPathView(): PathView = pathView
    override fun getSearchView(): SearchView = searchView
    override fun getFrame(): JFrame = frame
    override fun showErrorMessage(message: String) {
        JOptionPane.showMessageDialog(frame, message, "错误", JOptionPane.ERROR_MESSAGE)
    }

    override fun showInputDialog(title: String, message: String): String? {
        return JOptionPane.showInputDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE)
    }
}