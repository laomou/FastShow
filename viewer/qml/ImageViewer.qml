import QtQuick
import QtQuick.Window
import QtQuick.Controls
import QtQuick.Layouts

Window {
    id: root
    visible: true
    title: "Image Viewer"
    visibility: Window.FullScreen

    property var imagePaths: appController.imagePaths

    property int activeIndex: -1
    property var tileModel: imagePaths.map((path, idx) => ({
                                                               "index": idx,
                                                               "imageSource": path
                                                           }))

    function calculateGridLayout(count) {
        if (count <= 3) {
            return {
                "cols": count,
                "rows": 1
            }
        } else {
            const sqrt = Math.ceil(Math.sqrt(count))
            return {
                "cols": sqrt,
                "rows": Math.ceil(count / sqrt)
            }
        }
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

            ImageTile {
                color: repeater.count === 1 ? "black" : "#808080"
                index: modelData.index
                imageSource: modelData.imageSource
                isActive: root.activeIndex === index

                Layout.fillWidth: true
                Layout.fillHeight: true

                onToggleActive: function (index) {
                    root.activeIndex = (root.activeIndex === index) ? -1 : index
                }

                onTranslateUpdated: function (data) {
                    if (root.activeIndex !== data.sourceIndex) {
                        for (var i = 0; i < repeater.count; ++i) {
                            if (i !== data.sourceIndex) {
                                repeater.itemAt(i)?.translate(data.dx, data.dy)
                            }
                        }
                    }
                }

                onZoomUpdated: function (data) {
                    if (root.activeIndex !== data.sourceIndex) {
                        for (var i = 0; i < repeater.count; ++i) {
                            if (i !== data.sourceIndex) {
                                repeater.itemAt(i)?.zoom(data.factor)
                            }
                        }
                    }
                }

                onOverlayRequested: function (data) {
                    var sourceIndex = data.sourceIndex
                    var targetIndex = data.sourceIndex - 1
                    if (data.direction === "left") {
                        targetIndex = sourceIndex - 1
                    } else if (data.direction === "right") {
                        targetIndex = sourceIndex + 1
                    }
                    if (targetIndex >= 0 && targetIndex < repeater.count) {
                        var sourceTile = repeater.itemAt(sourceIndex)
                        var targetTile = repeater.itemAt(targetIndex)

                        if (data.overlay) {
                            targetTile.overlay(sourceTile)
                        } else {
                            targetTile.cancelOverlay()
                        }
                    }
                }
            }
        }
    }

    Shortcut {
        sequence: "Escape"
        onActivated: function () {
            Qt.quit()
        }
    }
}
