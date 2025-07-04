import QtQuick
import QtQuick.Window
import QtQuick.Controls
import QtQuick.Layouts

Window {
    id: root
    visible: true
    title: "Image Viewer"
    visibility: Window.FullScreen

    property var imagePaths: [
        "image://rgb/D:/Camera/IMG_20181024_143156_HDR.jpg",
        "image://rgb/D:/Camera/IMG_20181024_143156_HDR.jpg",
    ]

    property int activeIndex: -1
    property var tileModel: imagePaths.map((path, idx) => ({
        index: idx,
        imageSource: path
    }))

    function calculateGridLayout(count) {
        const sqrt = Math.ceil(Math.sqrt(count))
        return {cols: sqrt, rows: Math.ceil(count / sqrt)}
    }

    GridLayout {
        id: grid
        anchors.fill: parent
        columns: calculateGridLayout(imagePaths.length).cols
        rows: calculateGridLayout(imagePaths.length).rows
        columnSpacing: 2
        rowSpacing: 2
        
        Repeater {
            id: repeater
            model: tileModel

            delegate: ImageTile {
                index: modelData.index
                imageSource: modelData.imageSource
                isActive: root.activeIndex === index

                onToggleActive: function(index) {
                    root.activeIndex = (root.activeIndex === index) ? -1 : index
                }

                onTranslateUpdated: function(data) {
                    if (root.activeIndex !== data.sourceIndex) {
                        for (var i = 0; i < tileModel.length; ++i) {
                            if (i !== data.sourceIndex) {
                                repeater.itemAt(i)?.translate(data.dx, data.dy)
                            }
                        }
                    }
                }

                onZoomUpdated: function(data) {
                    if (root.activeIndex !== data.sourceIndex) {
                        for (var i = 0; i < tileModel.length; ++i) {
                            if (i !== data.sourceIndex) {
                                repeater.itemAt(i)?.zoom(data.factor)
                            }
                        }
                    }
                }

                Layout.fillWidth: true
                Layout.fillHeight: true
            }
        }
    }

    Shortcut {
        sequence: "Escape"
        onActivated: Qt.quit()
    }
}
