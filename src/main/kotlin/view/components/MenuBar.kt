package view.components

import presenter.MenuBarPresenter
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

interface MenuBar {
    fun setPresenter(presenter: MenuBarPresenter)
}

class MenuBarImpl(private val menuBar: JMenuBar) : MenuBar {
    private lateinit var presenter: MenuBarPresenter

    override fun setPresenter(presenter: MenuBarPresenter) {
        this.presenter = presenter
        val fileMenu = JMenu("文件").apply {
            add(JMenuItem("退出"))
        }
        val helpMenu = JMenu("帮助").apply {
            add(JMenuItem("关于"))
        }
        menuBar.add(fileMenu)
        menuBar.add(helpMenu)
    }
}