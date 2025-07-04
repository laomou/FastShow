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

    signal toggleActive(int index)
    signal translateUpdated(var data)
    signal zoomUpdated(var data)

    QtObject {
        id: internal
        property real minScale: 0.1
        property real maxScale: 50.0
        property bool dragging: false
        property point lastPos: Qt.point(0, 0)
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
                                                     scale * (1 + factor)))
    }

    function rotate(angle) {
        rotation += angle
    }

    Rectangle {
        id: toolbar
        width: parent.width
        height: 40
        z: 1

        RowLayout {
            anchors.fill: parent
            spacing: 5
            anchors.margins: 5

            RowLayout {
                spacing: 2
                Button {
                    text: "-"
                    onClicked: function () {
                        tile.zoom(-0.1)
                    }
                }
                Label {
                    text: "缩放: " + (tile.scale * 100).toFixed(0) + "%"
                    color: "black"
                }
                Button {
                    text: "+"
                    onClicked: function () {
                        tile.zoom(0.1)
                    }
                }
            }

            RowLayout {
                spacing: 2
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
                xScale: scale
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
            const factor = event.angleDelta.y > 0 ? 0.1 : -0.1

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
