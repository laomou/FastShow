package view.components

import presenter.PathPresenter
import javax.swing.JOptionPane
import javax.swing.JTextField
import javax.swing.SwingUtilities

interface PathView {
    fun setPresenter(presenter: PathPresenter)
    fun setCurrentPath(path: String)
    fun getCurrentPath(): String
    fun showError(message: String)
}

class PathViewImpl(private val textField: JTextField) : PathView {
    private lateinit var presenter: PathPresenter

    override fun setPresenter(presenter: PathPresenter) {
        this.presenter = presenter
        textField.addActionListener {
            presenter.onPathChanged()
        }
    }

    override fun setCurrentPath(path: String) {
        SwingUtilities.invokeLater {
            textField.text = path
        }
    }

    override fun getCurrentPath(): String {
        return textField.text
    }

    override fun showError(message: String) {
        SwingUtilities.invokeLater {
            JOptionPane.showMessageDialog(textField, message, "错误", JOptionPane.ERROR_MESSAGE)
        }
    }
}