package com.laomou.view.components

import com.laomou.presenter.ToolBarPresenter
import javax.swing.JToolBar

interface ToolBar {
    fun setPresenter(presenter: ToolBarPresenter)
}

class ToolBarImpl(private val toolBar: JToolBar) : ToolBar {
    private lateinit var presenter: ToolBarPresenter

    override fun setPresenter(presenter: ToolBarPresenter) {
        this.presenter = presenter
    }
}