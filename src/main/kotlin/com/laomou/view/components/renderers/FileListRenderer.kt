package com.laomou.view.components.renderers

import com.laomou.model.FileListNode
import com.laomou.utils.ThumbnailLoader
import java.awt.*
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

class FileListRenderer : JPanel(), ListCellRenderer<FileListNode> {
    private val iconLabel = JLabel()
    private val nameLabel = JLabel()
    private val directoryIcon: Icon? =
        javaClass.getResource("/icon/folder.png")?.let { ImageIcon(it) } ?: UIManager.getIcon("FileView.directoryIcon")
    private val fileIcon = UIManager.getIcon("FileView.fileIcon")
    private val defaultBorder = createRoundedBorder(Color.GRAY, false)
    private val selectedBorder = createRoundedBorder(Color.BLUE, true)

    init {
        layout = BorderLayout(5, 5)
        background = Color.WHITE
        border = defaultBorder

        iconLabel.horizontalAlignment = JLabel.CENTER
        nameLabel.horizontalAlignment = JLabel.CENTER

        add(iconLabel, BorderLayout.CENTER)
        add(nameLabel, BorderLayout.SOUTH)
        preferredSize = Dimension(140, 170)
    }

    override fun getListCellRendererComponent(
        list: JList<out FileListNode>,
        value: FileListNode,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        border = if (isSelected) selectedBorder else defaultBorder
        iconLabel.icon = value.icon ?: if (value.isDirectory) directoryIcon else fileIcon
        nameLabel.text = value.name ?: value.fileEntry.name
        toolTipText = value.name

        if (value.isImage && value.icon == null) {
            ThumbnailLoader.getThumbnail(value.fileEntry.file, 120, 120, { icon ->
                value.icon = icon
                SwingUtilities.invokeLater {
                    if (icon != null && isVisibleInViewport(list, index)) {
                        list.getCellBounds(index, index)?.let {
                            list.repaint(it)
                        }
                    }
                }
            })
        }

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
                g2.drawRoundRect(x + 1, y + 1, width - 2, height - 30, arc, arc)
                g2.dispose()
            }

            override fun getBorderInsets(c: Component) = Insets(thickness, thickness, thickness, thickness)
            override fun isBorderOpaque() = false
        }

        val padding = EmptyBorder(6, 6, 6, 6)
        val spacing = EmptyBorder(2, 2, 2, 2)
        return CompoundBorder(spacing, CompoundBorder(border, padding))
    }

    private fun isVisibleInViewport(list: JList<*>, index: Int): Boolean {
        val rect = list.visibleRect
        val cellBounds = list.getCellBounds(index, index)
        return cellBounds?.let { rect.intersects(it) } ?: false
    }

}