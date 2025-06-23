package view

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JTextField

class JSearchField : JTextField() {
    private var progress: Int = -1

    fun setProgress(value: Int) {
        progress = value
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g.create() as Graphics2D
        val barWidth = (width * (progress / 100.0)).toInt()
        if (progress == -1) {
            g2.color = Color(255, 255, 255)
        } else {
            g2.color = Color(135, 206, 250)
        }
        g2.fillRect(0, 0, barWidth, height)
        g2.dispose()

        super.paintComponent(g)
    }
}