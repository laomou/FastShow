package com.laomou.presenter

import com.laomou.mediator.FastShowMediator
import com.laomou.view.components.MenuBar

class MenuBarPresenter(
    private val view: MenuBar,
    private val mediator: FastShowMediator
)  {
    init {
        view.setPresenter(this)
    }
}