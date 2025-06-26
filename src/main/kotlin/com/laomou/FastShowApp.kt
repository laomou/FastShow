package com.laomou

import com.laomou.mediator.FastShowMediator
import com.laomou.model.DefaultFileSystemModel
import com.laomou.view.MainViewImpl
import java.awt.Font
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    SwingUtilities.invokeLater {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            UIManager.put("Label.font", Font(Font.DIALOG, Font.PLAIN, 12))
            UIManager.put("TextField.font", Font(Font.DIALOG, Font.PLAIN, 12))
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