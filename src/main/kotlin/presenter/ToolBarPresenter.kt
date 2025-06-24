package presenter

import mediator.FastShowMediator
import view.components.ToolBar

class ToolBarPresenter(
    private val view: ToolBar,
    private val mediator: FastShowMediator
) {
    init {
        view.setPresenter(this)
    }
}