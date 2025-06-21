package view.components.renderers

import model.FileModel
import java.awt.*
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

class FileListRenderer : JPanel(), ListCellRenderer<FileModel> {
    private val iconLabel = JLabel()
    private val nameLabel = JLabel()
    private val folderIcon = UIManager.getIcon("FileView.directoryIcon")
    private val fileIcon = UIManager.getIcon("FileView.fileIcon")
    private val defaultBorder = createRoundedBorder(Color.GRAY, false)
    private val selectedBorder = createRoundedBorder(Color.BLUE, true)

    init {
        layout = BorderLayout(5, 5)
        background = Color.WHITE
        border = defaultBorder

        iconLabel.horizontalAlignment = JLabel.CENTER
        nameLabel.horizontalAlignment = JLabel.CENTER
        nameLabel.font = Font("SansSerif", Font.PLAIN, 12)

        add(iconLabel, BorderLayout.CENTER)
        add(nameLabel, BorderLayout.SOUTH)
        preferredSize = Dimension(120, 120)
    }

    override fun getListCellRendererComponent(
        list: JList<out FileModel>,
        value: FileModel,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        border = if (isSelected) selectedBorder else defaultBorder
        iconLabel.icon = if (value.file.isDirectory) folderIcon else fileIcon
        nameLabel.text = value.name
        toolTipText = value.name

        return this
    }

    private fun createRoundedBorder(color: Color, isSelected: Boolean): Border {
        val border = object : Border {
            private val arc = 20
            private val thickness = 2

            override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
                val g2 = g.create() as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.color = color
                g2.stroke = BasicStroke(if (isSelected) 2f else 0.6f)
                g2.drawRoundRect(x + 1, y + 1, width - 2, height - 2, arc, arc)
                g2.dispose()
            }

            override fun getBorderInsets(c: Component) = Insets(thickness, thickness, thickness, thickness)
            override fun isBorderOpaque() = false
        }

        val padding = EmptyBorder(6, 6, 6, 6)
        val spacing = EmptyBorder(2, 2, 2, 2)
        return CompoundBorder(spacing, CompoundBorder(border, padding))
    }


}