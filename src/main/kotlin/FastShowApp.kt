import mediator.FastShowMediator
import model.DefaultFileSystemModel
import view.MainViewImpl
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    SwingUtilities.invokeLater {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fileSystemMode = DefaultFileSystemModel()
        val view = MainViewImpl()
        view.initializeUI()

        val mediator = FastShowMediator(fileSystemMode, view)
        mediator.initialize()
    }
}