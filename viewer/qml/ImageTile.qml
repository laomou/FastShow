import QtQuick
import QtQuick.Controls
import QtQuick.Layouts

Rectangle {
    id: tile
    property int index: -1
    property string imageSource
    property bool isActive: false
    property real scale: 1.0
    property real offsetX: 0
    property real offsetY: 0
    property real rotation: 0
    property bool flip_h: false

    signal toggleActive(int index)
    signal translateUpdated(var data)
    signal zoomUpdated(var data)
    signal overlayRequested(var data)

    QtObject {
        id: internal
        property real minScale: 0.1
        property real maxScale: 50.0
        property real scaleFactor: 1.1
        property bool dragging: false
        property point lastPos: Qt.point(0, 0)
        property var originalState: ({})
    }

    border {
        width: 1
        color: isActive ? "#FF3B30" : "#FFFFFF"
    }
    clip: true
    antialiasing: true

    function translate(dx, dy) {
        offsetX += dx
        offsetY += dy
    }

    function zoom(factor) {
        scale = Math.max(internal.minScale, Math.min(internal.maxScale,
                                                     scale * factor))
    }

    function rotate(angle) {
        rotation += angle
    }

    function flip_horizontal() {
        flip_h = !flip_h
    }

    function overlay(targetTile) {
        internal.originalState = {
            "imageSource": imageSource,
            "scale": scale,
            "offsetX": offsetX,
            "offsetY": offsetY,
            "rotation": rotation,
            "flip_h": flip_h
        }

        if (targetTile) {
            imageSource = targetTile.imageSource
            scale = targetTile.scale
            offsetX = targetTile.offsetX
            offsetY = targetTile.offsetY
            rotation = targetTile.rotation
            flip_h = targetTile.flip_h
        }
    }

    function cancelOverlay() {
        if (internal.originalState) {
            imageSource = internal.originalState.imageSource
            scale = internal.originalState.scale
            offsetX = internal.originalState.offsetX
            offsetY = internal.originalState.offsetY
            rotation = internal.originalState.rotation
            flip_h = internal.originalState.flip_h
        }
    }

    Rectangle {
        id: toolbar
        width: parent.width
        height: 40
        z: 1

        RowLayout {
            anchors.fill: parent
            spacing: 8
            anchors.margins: 2

            Row {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                Button {
                    text: "-"
                    onClicked: function () {
                        tile.zoom(1 / internal.scaleFactor)
                    }
                }
                Label {
                    text: "缩放: " + (tile.scale * 100).toFixed(0) + "%"
                    color: "black"
                }
                Button {
                    text: "+"
                    onClicked: function () {
                        tile.zoom(internal.scaleFactor)
                    }
                }
            }

            Row {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                Button {
                    text: "↺"
                    onClicked: function () {
                        tile.rotate(-90)
                    }
                }
                Button {
                    text: "↻"
                    onClicked: function () {
                        tile.rotate(90)
                    }
                }
            }

            Row {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                Button {
                    text: "水平翻转"
                    onClicked: function () {
                        tile.flip_horizontal()
                    }
                }
            }

            Row {
                spacing: 2
                Layout.alignment: Qt.AlignLeft
                Button {
                    visible: (index >= 1)
                    text: "向左叠加"
                    onPressed: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "left",
                                             "overlay": true
                                         })
                    }
                    onReleased: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "left",
                                             "overlay": false
                                         })
                    }
                }
                Button {
                    text: "向右叠加"
                    onPressed: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "right",
                                             "overlay": true
                                         })
                    }
                    onReleased: function () {
                        overlayRequested({
                                             "sourceIndex": index,
                                             "direction": "right",
                                             "overlay": false
                                         })
                    }
                }
            }

            Item { Layout.fillWidth: true }
        }
    }

    Image {
        id: contentImage
        anchors {
            left: parent.left
            right: parent.right
            top: toolbar.bottom
            bottom: parent.bottom
        }
        source: imageSource
        fillMode: Image.PreserveAspectFit
        smooth: true
        mipmap: true

        transform: [
            Translate {
                x: offsetX
                y: offsetY
            },
            Scale {
                origin.x: contentImage.width / 2
                origin.y: contentImage.height / 2
                xScale: flip_h ? scale * -1 : scale
                yScale: scale
            },
            Rotation {
                origin.x: contentImage.width / 2
                origin.y: contentImage.height / 2
                angle: rotation
            }
        ]
    }

    MouseArea {
        anchors {
            left: parent.left
            right: parent.right
            top: toolbar.bottom
            bottom: parent.bottom
        }
        acceptedButtons: Qt.LeftButton
        hoverEnabled: true
        scrollGestureEnabled: false

        onPressed: function (event) {
            if (event.button === Qt.LeftButton) {
                internal.dragging = true
                internal.lastPos = Qt.point(event.x, event.y)
            }
        }

        onReleased: function (event) {
            internal.dragging = false
        }

        onPositionChanged: function (event) {
            if (internal.dragging) {
                const dx = event.x - internal.lastPos.x
                const dy = event.y - internal.lastPos.y
                internal.lastPos = Qt.point(event.x, event.y)

                translate(dx, dy)

                translateUpdated({
                                     "sourceIndex": index,
                                     "dx": dx,
                                     "dy": dy
                                 })
            }
        }

        onWheel: function (event) {
            const delta = event.angleDelta.y > 0 ? -1 : 1
            const factor = delta > 0 ? 1 / internal.scaleFactor : internal.scaleFactor

            zoom(factor)

            zoomUpdated({
                            "sourceIndex": index,
                            "factor": factor
                        })

            event.accepted = true
        }

        onDoubleClicked: function () {
            toggleActive(index)
        }
    }
}
