package presenter

import mediator.FastShowMediator
import view.components.MenuBar

class MenuBarPresenter(
    private val view: MenuBar,
    private val mediator: FastShowMediator
)  {
    init {
        view.setPresenter(this)
    }
}