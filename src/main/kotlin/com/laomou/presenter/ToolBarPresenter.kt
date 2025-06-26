package com.laomou.presenter

import com.laomou.mediator.FastShowMediator
import com.laomou.view.components.ToolBar

class ToolBarPresenter(
    private val view: ToolBar,
    private val mediator: FastShowMediator
) {
    init {
        view.setPresenter(this)
    }
}